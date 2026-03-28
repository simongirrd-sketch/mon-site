package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Automatically exchanges pest drops at the Pest Control NPC.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AutoPestExchange implements IFeature {

    private static AutoPestExchange instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;

    public static AutoPestExchange getInstance() {
        if (instance == null) instance = new AutoPestExchange();
        return instance;
    }

    @Override
    public String getName() { return "AutoPestExchange"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInGarden()) {
            LogUtils.sendWarning("[AutoPestExchange] Not in Garden!");
            return;
        }
        running = true;
        tickDelay = 0;
        LogUtils.sendMessage("§a[AutoPestExchange] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[AutoPestExchange] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        if (InventoryUtils.isContainerOpen("Pest Control")) {
            int exchangeSlot = InventoryUtils.findContainerSlotByName("Exchange");
            if (exchangeSlot != -1) {
                mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        exchangeSlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP,
                        mc.player
                );
                tickDelay = 20;
                LogUtils.sendMessage("§a[AutoPestExchange] §fExchanged pest drops!");
            }
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
