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

import com.mojang.text2speech.Narrator;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test functionality within the {@link NarratorManagerMixin}.
 *
 * @author Case Walker
 */
public class NarratorManagerMixinTest {

    private static final NarratorManagerMixinTestImpl narratorManagerMixin = new NarratorManagerMixinTestImpl();
    private static final DummyNarrator narrator = new DummyNarrator();

    @BeforeAll
    static void setTheNarrator() {
        Whitebox.setInternalState(narratorManagerMixin, "narrator", narrator);
    }

    @BeforeEach
    void resetDependencies() {
        narratorManagerMixin.isAllChat = false;
        narrator.active = false;
        narrator.clear();
    }

    @Test
    @DisplayName("Nothing is narrated if the the narrator mode is not ALL_CHAT (onNarrateChatMessage)")
    void testWrongMode() {
        narratorManagerMixin.isAllChat = false;
        narrator.active = true;

        narratorManagerMixin.onNarrateChatMessage(() -> Text.of("text2"), new CallbackInfo("test", true));

        assertTrue(narrator.thingsSaid.isEmpty(),
                "Narrator should not get any narrations if the narrator mode is wrong. Received: " +
                        narrator.thingsSaid.stream().map(Pair::getLeft).toList());
    }

    @Test
    @DisplayName("Narration still runs if the narrator is not active? Odd behavior. (onNarrateChatMessage)")
    void testNarratorInactive() {
        narratorManagerMixin.isAllChat = true;
        narrator.active = false;

        narratorManagerMixin.onNarrateChatMessage(() -> Text.of("text2"), new CallbackInfo("test", true));

        assertEquals(1, narrator.thingsSaid.size(), "Narrator should have received 1 narration");
    }

    @Test
    @DisplayName("Narration succeeds (interrupt false) if the mod is enabled and narrator is active " +
            "(onNarrateChatMessage)")
    void testChatSucceeds() {
        narratorManagerMixin.isAllChat = true;
        narrator.active = true;
        CallbackInfo ci = new CallbackInfo("test", true);

        narratorManagerMixin.onNarrateChatMessage(() -> Text.of("text"), ci);

        assertEquals(1, narrator.thingsSaid.size(), "Narrator should have received 1 narration");
        assertFalse(narrator.thingsSaid.get(0).getRight(), "Interrupt should be false for chat message");
        assertTrue(ci.isCancelled(), "CallbackInfo should become cancelled if chat is narrated");
    }

    @Test
    @DisplayName("System messages succeed (interrupt false) if the mod is enabled and narrator is active " +
            "(onNarrateChatMessage)")
    void testSystemSucceeds() {
        narratorManagerMixin.isAllChat = true;
        narrator.active = true;
        CallbackInfo ci = new CallbackInfo("test", true);

        narratorManagerMixin.onNarrateChatMessage(() -> Text.of("text"), ci);

        assertEquals(1, narrator.thingsSaid.size(), "Narrator should have received 1 narration");
        assertFalse(narrator.thingsSaid.get(0).getRight(), "Interrupt should be false even for a system message");
        assertTrue( ci.isCancelled(), "CallbackInfo should become cancelled if chat is narrated");
    }

    @Test
    @DisplayName("The overridden method should not be cancelled when the mode is not ALL_CHAT (onNarrate)")
    void testNarrateWrongMode() {
        narratorManagerMixin.isAllChat = false;
        CallbackInfo ci = new CallbackInfo("test", true);

        narratorManagerMixin.onNarrate("String", ci);

        assertFalse(ci.isCancelled(), "CallbackInfo should not be cancelled if the narrator mode is not ALL_CHAT");
    }

    @Test
    @DisplayName("The overridden method should be cancelled when the mode is ALL_CHAT (onNarrate)")
    void testNarrateRightMode() {
        narratorManagerMixin.isAllChat = true;
        CallbackInfo ci = new CallbackInfo("test", true);

        narratorManagerMixin.onNarrate("String", ci);

        assertTrue(ci.isCancelled(), "CallbackInfo should be cancelled if the narrator mode is ALL_CHAT");
    }

    @Test
    @DisplayName("Forced narration does nothing if the mode is wrong (forceNarrateOnMode)")
    void testForceNarrateWrongMode() {
        narratorManagerMixin.isAllChat = false;

        narratorManagerMixin.forceNarrateOnMode(Text.of("force test 1"));

        assertTrue(narrator.thingsSaid.isEmpty(),
                "Narrator should not get any narrations when narration mode is wrong. Received: " +
                        narrator.thingsSaid.stream().map(Pair::getLeft).toList());
    }

    @Test
    @DisplayName("Forced narration works if the mode is right (forceNarrateOnMode)")
    void testForceNarrateWithRightMode() {
        narratorManagerMixin.isAllChat = true;

        narratorManagerMixin.forceNarrateOnMode(Text.of("force test 2"));

        assertEquals(1, narrator.thingsSaid.size(), "Narrator should have received 1 narration");
    }

    /**
     * Concrete implementation for the {@link NarratorManagerMixin} abstract class.
     */
    public static class NarratorManagerMixinTestImpl extends NarratorManagerMixin {
        public void debugPrintMessage(String var1) {}
        public boolean isAllChat;
        boolean narratorModeIsAllChat() { return isAllChat; }
    }

    /**
     * Class to mock the narrator.
     */
    private static class DummyNarrator implements Narrator {
        public boolean active;
        public List<Pair<String, Boolean>> thingsSaid = new ArrayList<>();

        public void say(String msg, boolean interrupt) { thingsSaid.add(new Pair<>(msg, interrupt)); }
        public void clear() { thingsSaid.clear(); }
        public boolean active() { return active; }
        public void destroy() {}
    }
}
