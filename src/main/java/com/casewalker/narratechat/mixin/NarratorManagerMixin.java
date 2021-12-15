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

import com.casewalker.narratechat.config.NarrateChatModConfig;
import com.casewalker.modutils.config.ConfigHandler;
import com.casewalker.modutils.interfaces.Reloadable;
import com.mojang.text2speech.Narrator;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

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
public abstract class NarratorManagerMixin implements Reloadable {

    @Shadow
    private Narrator narrator;

    @Shadow
    abstract void debugPrintMessage(String var1);

    /**
     * Handler for the mod configuration.
     */
    private ConfigHandler<NarrateChatModConfig> config;

    /**
     * Inject custom logic at the end of {@link NarratorManager#NarratorManager()} in order to initialize the config.
     *
     * @param ci {@link CallbackInfo} used by SpongePowered
     */
    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onInit(final CallbackInfo ci) {
        LOGGER.info("This line is printed by the Narrate Chat Mod mixin!");

        config = new ConfigHandler<>(NarrateChatModConfig.class);
        config.initialize();
        config.registerSubscriber(this);
    }

    /**
     * Inject a narration override at the head of {@link NarratorManager#onChatMessage(MessageType, Text, UUID)}. Force
     * it to narrate all chat and system messages (removing interrupts), and then skip the code of the real method.
     *
     * @param messageType Metadata about the narration text
     * @param message     Text to optionally narrate from the Minecraft chat
     * @param sender      Unused parameter from NarratorManager
     * @param ci          CallbackInfo used by SpongePowered
     */
    @SuppressWarnings("CastCanBeRemovedNarrowingVariableType")
    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onOnChatMessage(
            final MessageType messageType,
            final Text message,
            final UUID sender,
            final CallbackInfo ci) {

        // If the mod is not enabled, exit
        if (!config.get().isModEnabled()) {
            return;
        }

        // Copied mostly verbatim from NarratorManager#onChatMessage
        if (!narrator.active()) {
            debugPrintMessage(message.getString());

        } else {
            final Object text2;
            if (message instanceof TranslatableText && "chat.type.text".equals(((TranslatableText) message).getKey())) {
                text2 = new TranslatableText("chat.type.text.narrate", ((TranslatableText) message).getArgs());
            } else {
                text2 = message;
            }
            final String string = ((Text) text2).getString();
            debugPrintMessage(string);

            narrator.say(string, false);
            // If the mixin has narrated chat, then cancel the Minecraft call to NarratorManager#onChatMessage
            ci.cancel();
        }
    }

    @Override
    public void reload() {
        if (!narrator.active()) {
            debugPrintMessage("Updated configuration: " + config.get());
        } else {
            narrator.say("Updated configuration: " + config.get(), false);
        }
    }
}