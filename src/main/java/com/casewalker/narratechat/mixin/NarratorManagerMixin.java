/*
 * Licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 Case Walker.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.casewalker.narratechat.mixin;

import com.casewalker.narratechat.interfaces.ForcedNarratorManager;
import com.casewalker.narratechat.util.Util;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.text2speech.Narrator;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

import static com.casewalker.narratechat.NarrateChatMod.LOGGER;

/**
 * Mixin targeting the {@link NarratorManager} class to allow users to enable all narrations that pass through the
 * Minecraft chat.
 * <p>
 * The chat receives user chat messages as well as system messages. The existing narrator in Minecraft allows for three
 * narration modes:
 * <ul>
 *     <li>Narrates All</li>
 *     <li>Narrates Chat</li>
 *     <li>Narrates System</li>
 * </ul>
 * Some users believe "Narrates All" is too noisy, while choosing one of the other two options leaves out chat messages
 * which they would like to have narrated. Thus, this mixin enables narration on all messages which come to the
 * {@link NarratorManager} through the chat.
 *
 * @author Case Walker
 */
@Mixin(NarratorManager.class)
public abstract class NarratorManagerMixin implements ForcedNarratorManager {

    @Shadow
    @Final
    private Narrator narrator;

    @Shadow
    abstract protected void debugPrintMessage(String var1);

    @Shadow
    private NarratorMode getNarratorOption() {
        throw new AssertionError("Shadowed method wrapper 'getNarratorOption' should not run");
    }

    /**
     * Wrapper method to make testing easy and provide the answer this mod cares about, whether the current narrator
     * option is equal to the custom ALL_CHAT mode.
     *
     * @return The result of {@link #getNarratorOption()} == ALL_CHAT
     */
    @VisibleForTesting
    boolean narratorModeIsAllChat() {
        return getNarratorOption().equals(Util.allChat());
    }

    /**
     * Inject custom logic at the end of {@link NarratorManager#NarratorManager} in order to initialize the config.
     *
     * @param ci {@link CallbackInfo} used by SpongePowered
     */
    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onInit(final CallbackInfo ci) {
        LOGGER.info("This line is printed by the Narrate Chat Mod mixin!");
    }

    /**
     * Inject a narration override at the head of {@link
     * NarratorManager#narrateChatMessage(Supplier)}. Force it to narrate all chat and system
     * messages (removing interrupts), and then skip the code of the real method.
     *
     * @param messageSupplier Text (supplied) to optionally narrate from the Minecraft chat
     * @param ci CallbackInfo used by SpongePowered
     */
    @Inject(method = "narrateChatMessage", at = @At("HEAD"), cancellable = true)
    public void onNarrateChatMessage(final Supplier<Text> messageSupplier, final CallbackInfo ci) {

        // If the NarratorMode is anything other than the custom ALL_CHAT, exit without cancelling
        if (!narratorModeIsAllChat()) {
            return;
        }

        // Copied from NarratorManager#narrateChatMessage. TODO Why is there no 'this.narrator.active()' check?
        String string = messageSupplier.get().getString();
        this.debugPrintMessage(string);
        this.narrator.say(string, false);

        // If the mixin has performed narration, then cancel the Minecraft call to NarratorManager#narrateChatMessage
        ci.cancel();
    }

    /**
     * Inject a narration override at the head of {@link NarratorManager#narrate(String)}. The real method would always
     * exit if the {@link NarratorMode} was CHAT, and all system messages which this mod cares about are already handled
     * in {@link NarratorManager#narrateChatMessage(Supplier)}, thus when the ALL_CHAT mode is active, this method
     * should also exit and force the real method to be cancelled as well.
     *
     * @param text Text to be conditionally narrated
     * @param ci CallbackInfo used by SpongePowered
     */
    @Inject(method = "narrate(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void onNarrate(final String text, final CallbackInfo ci) {
        if (narratorModeIsAllChat()) {
            ci.cancel();
        }
    }

    @Override
    public void forceNarrateOnMode(final Text text) {
        if (narratorModeIsAllChat()) {
            this.narrator.say(text.getString(), false);
        }
    }
}
