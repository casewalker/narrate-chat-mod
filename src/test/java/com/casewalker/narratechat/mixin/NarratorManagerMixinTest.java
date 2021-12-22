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

import com.casewalker.modutils.config.ConfigHandler;
import com.casewalker.narratechat.config.NarrateChatModConfig;
import com.mojang.text2speech.Narrator;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.minecraft.network.MessageType.CHAT;
import static net.minecraft.network.MessageType.SYSTEM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test functionality within the {@link NarratorManagerMixin}.
 *
 * @author Case Walker
 */
class NarratorManagerMixinTest {

    private static final NarratorManagerMixinTestImpl narratorManagerMixin = new NarratorManagerMixinTestImpl();
    private static final ConfigHandler<NarrateChatModConfig> config =
            new ConfigHandler<>(NarrateChatModConfig.class);
    private static final UUID UUID_VALUE = UUID.fromString("12345678-1234-1234-1234-123456789012");

    @BeforeAll
    static void initializeDependencies() {
        config.initialize(List.of(Path.of("src", "test", "resources", "narratechatmod.json")));
        Whitebox.setInternalState(narratorManagerMixin, "configNCM2", config);
    }

    @BeforeEach
    void resetConfig() {
        config.get().setModEnabled(false);
    }

    @Test
    @DisplayName("Nothing is narrated if the mod is disabled (onOnChatMessage)")
    public void testModDisabled() {
        DummyNarrator narrator = new DummyNarrator();
        narrator.active = true;
        narratorManagerMixin.setNarrator(narrator);

        narratorManagerMixin.onOnChatMessage(CHAT, Text.of("text2"), UUID_VALUE, new CallbackInfo("test", true));

        assertTrue(narrator.thingsSaid.isEmpty(),
                "Narrator should not get any narrations if mod is disabled: " +
                        narrator.thingsSaid.stream().map(Pair::getLeft).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("Nothing is narrated if the narrator is not active (onOnChatMessage)")
    public void testNarratorInactive() {
        config.get().setModEnabled(true);
        DummyNarrator narrator = new DummyNarrator();
        narrator.active = false;
        narratorManagerMixin.setNarrator(narrator);

        narratorManagerMixin.onOnChatMessage(CHAT, Text.of("text2"), UUID_VALUE, new CallbackInfo("test", true));

        assertTrue(narrator.thingsSaid.isEmpty(),
                "Narrator should not get any narrations when narrator is not active: " +
                        narrator.thingsSaid.stream().map(Pair::getLeft).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("Narration succeeds (interrupt false) if the mod is enabled and narrator is active (onOnChatMessage)")
    public void testChatSucceeds() {
        config.get().setModEnabled(true);
        DummyNarrator narrator = new DummyNarrator();
        narrator.active = true;
        narratorManagerMixin.setNarrator(narrator);
        CallbackInfo ci = new CallbackInfo("test", true);

        narratorManagerMixin.onOnChatMessage(CHAT, Text.of("text"), UUID_VALUE, ci);

        assertEquals(1, narrator.thingsSaid.size(), "Narrator should have received 1 narration");
        assertFalse(narrator.thingsSaid.get(0).getRight(), "Interrupt should be false for chat message");
        assertTrue(ci.isCancelled(), "CallbackInfo should become canceled if chat is narrated");
    }

    @Test
    @DisplayName("System messages also succeed (interrupt false) if the mod is enabled and narrator is active " +
            "(onOnChatMessage)")
    public void testSystemSucceeds() {
        config.get().setModEnabled(true);
        DummyNarrator narrator = new DummyNarrator();
        narrator.active = true;
        narratorManagerMixin.setNarrator(narrator);
        CallbackInfo ci = new CallbackInfo("test", true);

        narratorManagerMixin.onOnChatMessage(SYSTEM, Text.of("text"), UUID_VALUE, ci);

        assertEquals(1, narrator.thingsSaid.size(), "Narrator should have received 1 narration");
        assertFalse(narrator.thingsSaid.get(0).getRight(), "Interrupt should be false even for a system message");
        assertTrue(ci.isCancelled(), "CallbackInfo should become canceled if chat is narrated");
    }

    /**
     * Concrete implementation for the {@link NarratorManagerMixin} abstract class.
     */
    private static class NarratorManagerMixinTestImpl extends NarratorManagerMixin {
        void debugPrintMessage(String var1) {}
        void setNarrator(Narrator narrator) { Whitebox.setInternalState(this, "narrator", narrator); }
    }

    /**
     * Class to mock the narrator.
     */
    private static class DummyNarrator implements Narrator {
        public boolean active;
        public List<Pair<String, Boolean>> thingsSaid = new ArrayList<>();

        public void say(String msg, boolean interrupt) { thingsSaid.add(new Pair<>(msg, interrupt)); }
        public void clear() {}
        public boolean active() { return active; }
        public void destroy() {}
    }
}
