package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.config.FarmHelperConfig;
import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Automatically sells items on the Hypixel Bazaar.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AutoBazaar implements IFeature {

    private static AutoBazaar instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;

    public static AutoBazaar getInstance() {
        if (instance == null) instance = new AutoBazaar();
        return instance;
    }

    @Override
    public String getName() { return "AutoBazaar"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInSkyblock()) {
            LogUtils.sendWarning("[AutoBazaar] Not on Hypixel Skyblock!");
            return;
        }
        running = true;
        tickDelay = 0;
        LogUtils.sendMessage("§a[AutoBazaar] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[AutoBazaar] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        // Check if Bazaar GUI is open
        if (InventoryUtils.isContainerOpen("Bazaar")) {
            handleBazaarGui();
        }
    }

    private void handleBazaarGui() {
        // Find sell order slot and click it
        int sellSlot = InventoryUtils.findContainerSlotByName("Sell Inventory Now");
        if (sellSlot != -1) {
            // Simulate click on sell slot
            mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    sellSlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP,
                    mc.player
            );
            tickDelay = 20; // Wait 1 second between actions
            LogUtils.sendMessage("§a[AutoBazaar] §fSold items!");
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
