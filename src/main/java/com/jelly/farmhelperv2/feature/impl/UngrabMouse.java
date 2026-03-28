package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Releases the mouse cursor so you can use other applications while farming.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * In Fabric 1.21, mouse grab is controlled via Mouse.unlockCursor() / lockCursor().
 */
public class UngrabMouse implements IFeature {

    private static UngrabMouse instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    public static UngrabMouse getInstance() {
        if (instance == null) instance = new UngrabMouse();
        return instance;
    }

    @Override
    public String getName() { return "UngrabMouse"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        running = true;
        // Unlock the mouse cursor
        mc.mouse.unlockCursor();
        LogUtils.sendMessage("§a[UngrabMouse] §fMouse released. You can now use other apps.");
    }

    @Override
    public void stop() {
        running = false;
        // Re-lock the mouse cursor
        if (mc.currentScreen == null) {
            mc.mouse.lockCursor();
        }
        LogUtils.sendMessage("§a[UngrabMouse] §fMouse re-locked.");
    }

    @Override
    public void onTick() {
        // Keep the mouse unlocked each tick (game tries to re-lock it)
        if (running && mc.currentScreen == null) {
            mc.mouse.unlockCursor();
        }
    }

    @Override
    public void onWorldLoad() { if (running) stop(); }
}
