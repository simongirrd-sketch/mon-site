package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Automatically equips armor from the wardrobe based on configured slot.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AutoWardrobe implements IFeature {

    private static AutoWardrobe instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;
    private int targetSlot = 1; // Wardrobe page slot (1-indexed)

    public static AutoWardrobe getInstance() {
        if (instance == null) instance = new AutoWardrobe();
        return instance;
    }

    public void setTargetSlot(int slot) { this.targetSlot = slot; }

    @Override
    public String getName() { return "AutoWardrobe"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInSkyblock()) {
            LogUtils.sendWarning("[AutoWardrobe] Not on Hypixel Skyblock!");
            return;
        }
        running = true;
        tickDelay = 0;
        LogUtils.sendMessage("§a[AutoWardrobe] §fStarted. Target slot: " + targetSlot);
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[AutoWardrobe] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        if (InventoryUtils.isContainerOpen("Wardrobe")) {
            // Slots 0-8 are the 9 wardrobe slots in the first row
            int slot = targetSlot - 1;
            if (slot >= 0 && slot < 9) {
                mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        slot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP,
                        mc.player
                );
                tickDelay = 20;
                stop(); // One-shot operation
                LogUtils.sendMessage("§a[AutoWardrobe] §fEquipped armor from slot " + targetSlot);
            }
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
