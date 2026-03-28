package com.jelly.farmhelperv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * KeyBinding utility methods for simulating key presses.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Key API changes:
 * - KeyBinding.setKeyBindState() -> KeyBinding.setKeyPressed()
 * - In Fabric, you directly use KeyBinding.setKeyPressed with the InputUtil.Key
 */
public class KeyBindUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Simulate pressing a key binding.
     * In 1.8.9 Forge: KeyBinding.setKeyBindState(key.getKeyCode(), true)
     * In Fabric 1.21: KeyBinding.setKeyPressed(key.getDefaultKey(), true)
     */
    public static void holdKey(KeyBinding keyBinding, boolean pressed) {
        KeyBinding.setKeyPressed(keyBinding.getDefaultKey(), pressed);
        keyBinding.setPressed(pressed);
    }

    public static void pressKey(KeyBinding keyBinding) {
        holdKey(keyBinding, true);
    }

    public static void releaseKey(KeyBinding keyBinding) {
        holdKey(keyBinding, false);
    }

    public static void releaseAllMovementKeys() {
        if (mc.options == null) return;
        holdKey(mc.options.forwardKey, false);
        holdKey(mc.options.backKey, false);
        holdKey(mc.options.leftKey, false);
        holdKey(mc.options.rightKey, false);
        holdKey(mc.options.jumpKey, false);
        holdKey(mc.options.sneakKey, false);
        holdKey(mc.options.sprintKey, false);
    }

    public static void holdForward(boolean held) {
        if (mc.options != null) holdKey(mc.options.forwardKey, held);
    }

    public static void holdBack(boolean held) {
        if (mc.options != null) holdKey(mc.options.backKey, held);
    }

    public static void holdLeft(boolean held) {
        if (mc.options != null) holdKey(mc.options.leftKey, held);
    }

    public static void holdRight(boolean held) {
        if (mc.options != null) holdKey(mc.options.rightKey, held);
    }

    public static void holdJump(boolean held) {
        if (mc.options != null) holdKey(mc.options.jumpKey, held);
    }

    public static void holdSneak(boolean held) {
        if (mc.options != null) holdKey(mc.options.sneakKey, held);
    }

    public static void holdSprint(boolean held) {
        if (mc.options != null) holdKey(mc.options.sprintKey, held);
    }

    public static void holdAttack(boolean held) {
        if (mc.options != null) holdKey(mc.options.attackKey, held);
    }

    public static void holdUse(boolean held) {
        if (mc.options != null) holdKey(mc.options.useKey, held);
    }
}
