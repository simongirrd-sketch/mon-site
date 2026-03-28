package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Automatically uses the Sprayonator to apply sprays on crops.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AutoSprayonator implements IFeature {

    private static AutoSprayonator instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;
    private int sprayDuration = 0;
    private static final int SPRAY_COOLDOWN_TICKS = 1800; // 90 seconds

    public static AutoSprayonator getInstance() {
        if (instance == null) instance = new AutoSprayonator();
        return instance;
    }

    @Override
    public String getName() { return "AutoSprayonator"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInGarden()) {
            LogUtils.sendWarning("[AutoSprayonator] Not in Garden!");
            return;
        }
        running = true;
        sprayDuration = SPRAY_COOLDOWN_TICKS;
        LogUtils.sendMessage("§a[AutoSprayonator] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[AutoSprayonator] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (tickDelay > 0) { tickDelay--; return; }
        if (sprayDuration > 0) { sprayDuration--; return; }

        // Time to spray again
        useSprayonator();
    }

    private void useSprayonator() {
        // Find Sprayonator in inventory (it's a custom item in Skyblock)
        // Check by item name
        int slot = findSprayonatorSlot();
        if (slot == -1) {
            LogUtils.sendWarning("[AutoSprayonator] Sprayonator not found in inventory!");
            stop();
            return;
        }

        mc.player.getInventory().selectedSlot = slot;
        mc.options.useKey.setPressed(true);
        tickDelay = 5;
        sprayDuration = SPRAY_COOLDOWN_TICKS;
        LogUtils.sendMessage("§a[AutoSprayonator] §fUsed Sprayonator!");
    }

    private int findSprayonatorSlot() {
        for (int i = 0; i < 9; i++) {
            net.minecraft.item.ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getName().getString().contains("Sprayonator")) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onWorldLoad() { stop(); }
}
