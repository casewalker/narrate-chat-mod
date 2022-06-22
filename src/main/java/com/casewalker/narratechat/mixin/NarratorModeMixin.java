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

import com.casewalker.modutils.util.NarratorModeMixinHelperParent;
import com.casewalker.narratechat.util.Util;
import net.minecraft.client.option.NarratorMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Mixin to add a new {@link NarratorMode}. Utilizing the {@link NarratorModeMixinHelperParent} class internally.
 *
 * @author Case Walker
 */
@Mixin(NarratorMode.class)
@Unique
public abstract class NarratorModeMixin {

    /**
     * Private class extending the {@link NarratorModeMixinHelperParent} to connect the {@link
     * NarratorModeMixinHelperParent#addNarratorMode(NarratorMode[], NarratorMode[], String, int, String)} method to the
     * {@link Invoker} constructor of the mixin.
     */
    private static class NarratorModeMixinHelper extends NarratorModeMixinHelperParent {
        @Override
        public NarratorMode narratorModeInvokeInit(
                final String internalName,
                final int internalId,
                final int id,
                final String name) {
            return NarratorModeMixin.invokeInit(internalName, internalId, id, name);
        }
    }

    /**
     * Shadow the internal enum field <code>field_18183</code> from the bytecode of {@link NarratorMode}.
     */
    @SuppressWarnings("target")
    @Shadow
    @Final
    @Mutable
    private static NarratorMode[] field_18183;

    /**
     * Shadow the <code>VALUES</code> field from {@link NarratorMode}.
     */
    @Shadow
    @Final
    @Mutable
    private static NarratorMode[] VALUES;

    /**
     * Helper containing the method used to add a new {@link NarratorMode} to the existing enum.
     */
    private final static NarratorModeMixinHelper HELPER = new NarratorModeMixinHelper();

    /**
     * The new custom {@link NarratorMode}.
     */
    private static final NarratorMode ALL_CHAT =
            addNarratorMode("ALL_CHAT", Util.ALL_CHAT_ID, "options.narrator.all_chat");

    /**
     * Invoke the constructor of the {@link NarratorMode} enum.
     */
    @Invoker("<init>")
    public static NarratorMode invokeInit(
            final String internalName,
            final int internalId,
            final int id,
            final String name) {
        throw new AssertionError("Invoked method wrapper 'invokeInit' should not run");
    }

    /**
     * Call the method {@link
     * NarratorModeMixinHelperParent#addNarratorMode(NarratorMode[], NarratorMode[], String, int, String)} and handle
     * its output correctly by overwriting the values of {@link NarratorModeMixin#VALUES} and {@link
     * NarratorModeMixin#field_18183} and then returning the newly created {@link NarratorMode}.
     *
     * @param internalName See {@link
     * NarratorModeMixinHelperParent#addNarratorMode(NarratorMode[], NarratorMode[], String, int, String)}
     * @param id The numeric value ID for the new NarratorMode
     * @param name The translatable name of the NarratorMode
     * @return The newly created NarratorMode
     */
    private static NarratorMode addNarratorMode(final String internalName, final int id, final String name) {
        final Object[] output = HELPER.addNarratorMode(VALUES, field_18183, internalName, id, name);

        VALUES      = (NarratorMode[]) output[0];
        field_18183 = (NarratorMode[]) output[1];
        return        (NarratorMode)   output[2];
    }
}
