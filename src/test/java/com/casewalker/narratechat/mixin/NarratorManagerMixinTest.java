///*
// * Licensed under the MIT License (MIT).
// *
// * Copyright (c) 2021 Case Walker.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package com.casewalker.narratechat.mixin;
//
//import com.mojang.text2speech.Narrator;
//import net.minecraft.network.message.MessageType;
//import net.minecraft.text.Text;
//import net.minecraft.util.Pair;
//import net.minecraft.util.registry.RegistryKey;
//import org.easymock.EasyMock;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.powermock.api.easymock.PowerMock;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
//import org.powermock.modules.junit4.PowerMockRunner;
//import org.powermock.reflect.Whitebox;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static net.minecraft.network.message.MessageType.NarrationRule.Kind.CHAT;
//import static net.minecraft.network.message.MessageType.NarrationRule.Kind.SYSTEM;
//
///**
// * Test functionality within the {@link NarratorManagerMixin}.
// *
// * @author Case Walker
// */
//@PrepareForTest({
//        MessageType.class,
//        MessageType.NarrationRule.class,
//        RegistryKey.class
//})
//@SuppressStaticInitializationFor("net.minecraft.util.registry.Registry")
//@RunWith(PowerMockRunner.class)
//public class NarratorManagerMixinTest {
//
//    private static NarratorManagerMixinTestImpl narratorManagerMixin;
//    private static MessageType chatType;
//    private static MessageType systemType;
//
//    @BeforeClass
//    public static void initialize() {
//        narratorManagerMixin =
//                PowerMock.createPartialMock(NarratorManagerMixinTestImpl.class, "narratorModeIsAllChat");
//
//        // This remaining nonsense is because Mojang introduced "MessageType" as a Record (I believe serialized) for
//        // 1.19, and it proved to be very difficult to mock. Mockito complained, PowerMockRunner doesn't exist for
//        // JUnit5, so I had to downgrade to JUnit4, put the "add-exports" and "add-opens" in gradle, etc.
//        // This is copied from NarratorConfigsMod.
//        // It's giving depression. But now it should work.
//        PowerMock.mockStatic(RegistryKey.class);
//        EasyMock.expect(RegistryKey.of(EasyMock.anyObject(), EasyMock.anyObject())).andStubReturn(null);
//        PowerMock.replay(RegistryKey.class);
//
//        final Optional<MessageType.NarrationRule> chatRule = Optional.of(MessageType.NarrationRule.of(CHAT));
//        final Optional<MessageType.NarrationRule> systemRule = Optional.of(MessageType.NarrationRule.of(SYSTEM));
//        chatType = PowerMock.createMock(MessageType.class, Optional.empty(), Optional.empty(), chatRule);
//        systemType = PowerMock.createMock(MessageType.class, Optional.empty(), Optional.empty(), systemRule);
//
//        EasyMock.expect(chatType.narration()).andStubReturn(Optional.of(MessageType.NarrationRule.of(CHAT)));
//        EasyMock.expect(systemType.narration()).andStubReturn(Optional.of(MessageType.NarrationRule.of(SYSTEM)));
//        PowerMock.replayAll(chatType, systemType);
//
//    }
//
//    @Before
//    public void initializeDependencies() {
//        EasyMock.reset(narratorManagerMixin);
//    }
//
//    @Test
////    @DisplayName("Nothing is narrated if the the narrator mode is not ALL_CHAT (onOnChatMessage)")
//    public void testWrongMode() {
//        EasyMock.expect(narratorManagerMixin.narratorModeIsAllChat()).andStubReturn(false);
//        EasyMock.replay(narratorManagerMixin);
//        DummyNarrator narrator = new DummyNarrator();
//        narrator.active = true;
//        narratorManagerMixin.setNarrator(narrator);
//
//        narratorManagerMixin.onOnChatMessage(chatType, Text.of("text2"), null, new CallbackInfo("test", true));
//
//        assertTrue(narrator.thingsSaid.isEmpty(),
//                "Narrator should not get any narrations if the narrator mode is wrong. Received: " +
//                        narrator.thingsSaid.stream().map(Pair::getLeft).toList());
//    }
//
//    @Test
////    @DisplayName("Nothing is narrated if the narrator is not active (onOnChatMessage)")
//    public void testNarratorInactive() {
//        EasyMock.expect(narratorManagerMixin.narratorModeIsAllChat()).andStubReturn(true);
//        EasyMock.replay(narratorManagerMixin);
//        DummyNarrator narrator = new DummyNarrator();
//        narrator.active = false;
//        narratorManagerMixin.setNarrator(narrator);
//
//        narratorManagerMixin.onOnChatMessage(chatType, Text.of("text2"), null, new CallbackInfo("test", true));
//
//        assertTrue(narrator.thingsSaid.isEmpty(),
//                "Narrator should not get any narrations when narrator is not active. Received: " +
//                        narrator.thingsSaid.stream().map(Pair::getLeft).toList());
//    }
//
//    @Test
////    @DisplayName("Narration succeeds (interrupt false) if the mod is enabled and narrator is active (onOnChatMessage)")
//    public void testChatSucceeds() {
//        EasyMock.expect(narratorManagerMixin.narratorModeIsAllChat()).andReturn(true);
//        EasyMock.replay(narratorManagerMixin);
//        DummyNarrator narrator = new DummyNarrator();
//        narrator.active = true;
//        narratorManagerMixin.setNarrator(narrator);
//        CallbackInfo ci = new CallbackInfo("test", true);
//
//        narratorManagerMixin.onOnChatMessage(chatType, Text.of("text"), null, ci);
//
//        assertEquals(1, narrator.thingsSaid.size(), "Narrator should have received 1 narration");
//        assertFalse(narrator.thingsSaid.get(0).getRight(), "Interrupt should be false for chat message");
//        assertTrue(ci.isCancelled(), "CallbackInfo should become cancelled if chat is narrated");
//    }
//
//    @Test
////    @DisplayName("System messages succeed (interrupt false) if the mod is enabled and narrator is active " +
////            "(onOnChatMessage)")
//    public void testSystemSucceeds() {
//        EasyMock.expect(narratorManagerMixin.narratorModeIsAllChat()).andReturn(true);
//        EasyMock.replay(narratorManagerMixin);
//        DummyNarrator narrator = new DummyNarrator();
//        narrator.active = true;
//        narratorManagerMixin.setNarrator(narrator);
//        CallbackInfo ci = new CallbackInfo("test", true);
//
//        narratorManagerMixin.onOnChatMessage(systemType, Text.of("text"), null, ci);
//
//        assertEquals(1, narrator.thingsSaid.size(), "Narrator should have received 1 narration");
//        assertFalse(narrator.thingsSaid.get(0).getRight(), "Interrupt should be false even for a system message");
//        assertTrue( ci.isCancelled(), "CallbackInfo should become cancelled if chat is narrated");
//    }
//
//    @Test
////    @DisplayName("The overridden method should not be cancelled when the mode is not ALL_CHAT (onNarrate)")
//    public void testNarrateWrongMode() {
//        EasyMock.expect(narratorManagerMixin.narratorModeIsAllChat()).andReturn(false);
//        EasyMock.replay(narratorManagerMixin);
//        CallbackInfo ci = new CallbackInfo("test", true);
//
//        narratorManagerMixin.onNarrate("String", ci);
//
//        assertFalse(ci.isCancelled(), "CallbackInfo should not be cancelled if the narrator mode is not ALL_CHAT");
//    }
//
//    @Test
////    @DisplayName("The overridden method should be cancelled when the mode is ALL_CHAT (onNarrate)")
//    public void testNarrateRightMode() {
//        EasyMock.expect(narratorManagerMixin.narratorModeIsAllChat()).andReturn(true);
//        EasyMock.replay(narratorManagerMixin);
//        CallbackInfo ci = new CallbackInfo("test", true);
//
//        narratorManagerMixin.onNarrate("String", ci);
//
//        assertTrue(ci.isCancelled(), "CallbackInfo should be cancelled if the narrator mode is ALL_CHAT");
//    }
//
//    /**
//     * Concrete implementation for the {@link NarratorManagerMixin} abstract class.
//     */
//    public static class NarratorManagerMixinTestImpl extends NarratorManagerMixin {
//        void debugPrintMessage(String var1) {}
//        void setNarrator(Narrator narrator) { Whitebox.setInternalState(this, "narrator", narrator); }
//    }
//
//    /**
//     * Class to mock the narrator.
//     */
//    private static class DummyNarrator implements Narrator {
//        public boolean active;
//        public List<Pair<String, Boolean>> thingsSaid = new ArrayList<>();
//
//        public void say(String msg, boolean interrupt) { thingsSaid.add(new Pair<>(msg, interrupt)); }
//        public void clear() {}
//        public boolean active() { return active; }
//        public void destroy() {}
//    }
//
//    /**
//     * Invert the assertion argument order because JUnit5 is better than JUnit4.
//     */
//    private void assertTrue(boolean b, String s) { Assert.assertTrue(s, b); }
//    private void assertFalse(boolean b, String s) { Assert.assertFalse(s, b); }
//    private void assertEquals(int x, int y, String s) { Assert.assertEquals(s, x, y); }
//}
