package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Automatically buys and uses Booster Cookies from the Bazaar.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AutoCookie implements IFeature {

    private static AutoCookie instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;

    public static AutoCookie getInstance() {
        if (instance == null) instance = new AutoCookie();
        return instance;
    }

    @Override
    public String getName() { return "AutoCookie"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInSkyblock()) {
            LogUtils.sendWarning("[AutoCookie] Not on Hypixel Skyblock!");
            return;
        }
        running = true;
        tickDelay = 0;
        LogUtils.sendMessage("§a[AutoCookie] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[AutoCookie] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        // Check if Booster Cookie GUI is open
        if (InventoryUtils.isContainerOpen("Booster Cookie")) {
            int buySlot = InventoryUtils.findContainerSlotByName("Buy Instantly");
            if (buySlot != -1) {
                mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        buySlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP,
                        mc.player
                );
                tickDelay = 40;
                LogUtils.sendMessage("§a[AutoCookie] §fPurchased Booster Cookie!");
            }
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
