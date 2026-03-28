package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Automatically sells crops at the NPC merchant.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AutoSell implements IFeature {

    private static AutoSell instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;

    public static AutoSell getInstance() {
        if (instance == null) instance = new AutoSell();
        return instance;
    }

    @Override
    public String getName() { return "AutoSell"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInSkyblock()) {
            LogUtils.sendWarning("[AutoSell] Not on Hypixel Skyblock!");
            return;
        }
        running = true;
        tickDelay = 0;
        LogUtils.sendMessage("§a[AutoSell] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[AutoSell] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        if (InventoryUtils.isContainerOpen("Sell") || InventoryUtils.isContainerOpen("Shop")) {
            int sellAllSlot = InventoryUtils.findContainerSlotByName("Sell All");
            if (sellAllSlot != -1) {
                mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        sellAllSlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP,
                        mc.player
                );
                tickDelay = 20;
                LogUtils.sendMessage("§a[AutoSell] §fSold all items!");
            }
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
