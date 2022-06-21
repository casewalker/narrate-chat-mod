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
package com.casewalker.narratechat.util;

import net.minecraft.client.option.NarratorMode;

/**
 * Utilities and constants for this mod.
 *
 * @author Case Walker
 */
public class Util {

    /**
     * Use a hard-coded ID for the ALL_CHAT mode defined in {@link com.casewalker.narratechat.mixin.NarratorModeMixin}.
     */
    public static final int ALL_CHAT_ID = 4;

    /**
     * Get the ALL_CHAT mode defined in {@link com.casewalker.narratechat.mixin.NarratorModeMixin}.
     *
     * @return The ALL_CHAT narrator mode
     */
    public static NarratorMode allChat() {
        return NarratorMode.byId(ALL_CHAT_ID);
    }
}
