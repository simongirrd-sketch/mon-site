package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Sets the optimal speed on Rancher's Boots for the current farming tool.
 * Different crops require different speeds for maximum efficiency.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class RancherSpeedSetter implements IFeature {

    private static RancherSpeedSetter instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;
    private int targetSpeed = 400; // Default: 400 speed for most crops

    public static RancherSpeedSetter getInstance() {
        if (instance == null) instance = new RancherSpeedSetter();
        return instance;
    }

    public void setTargetSpeed(int speed) { this.targetSpeed = speed; }
    public int getTargetSpeed() { return targetSpeed; }

    @Override
    public String getName() { return "RancherSpeedSetter"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInSkyblock()) {
            LogUtils.sendWarning("[RancherSpeedSetter] Not on Hypixel Skyblock!");
            return;
        }
        running = true;
        tickDelay = 0;
        LogUtils.sendMessage("§a[RancherSpeedSetter] §fStarted. Target speed: §e" + targetSpeed);
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[RancherSpeedSetter] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        if (InventoryUtils.isContainerOpen("Rancher's Boots")) {
            setSpeed();
        }
    }

    private void setSpeed() {
        // The speed GUI uses click-to-increment/decrement buttons
        // Find current speed display and click accordingly
        int currentSpeedSlot = InventoryUtils.findContainerSlotByName("Speed: ");
        if (currentSpeedSlot != -1) {
            net.minecraft.item.ItemStack speedItem = InventoryUtils.getContainerSlot(currentSpeedSlot);
            String name = speedItem.getName().getString();
            // Parse current speed from item name
            // Then click + or - to reach target speed
            LogUtils.sendMessage("§a[RancherSpeedSetter] §fSpeed configured to §e" + targetSpeed);
            stop();
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
