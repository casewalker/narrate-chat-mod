/*
 * Licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 Case Walker.
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.casewalker.narratechat.NarrateChatMod.LOGGER;

/**
 * Mixin targeting the {@link MessageHandler} class to allow system messages to be narrated properly by this mod.
 *
 * @author Case Walker
 */
@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    /**
     * Inject a narration override at the end of {@link MessageHandler#onGameMessage(Text, boolean)} to ensure that game
     * messages which are correctly handled by the mod.
     * 
     * @param message Text to be conditionally narrated
     * @param overlay Boolean used in the parent method
     * @param ci CallbackInfo used by SpongePowered
     */
    @Inject(method = "onGameMessage", at = @At("TAIL"))
    public void onOnGameMessage(final Text message, final boolean overlay, final CallbackInfo ci) {
        ((ForcedNarratorManager) this.client.getNarratorManager()).forceNarrateOnMode(message);
    }
}
