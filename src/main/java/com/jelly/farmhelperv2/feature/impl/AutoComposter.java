package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

/**
 * Automatically fills the Garden composter with organic matter and fuel.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AutoComposter implements IFeature {

    private static AutoComposter instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;

    public static AutoComposter getInstance() {
        if (instance == null) instance = new AutoComposter();
        return instance;
    }

    @Override
    public String getName() { return "AutoComposter"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInGarden()) {
            LogUtils.sendWarning("[AutoComposter] Not in Garden!");
            return;
        }
        running = true;
        tickDelay = 0;
        LogUtils.sendMessage("§a[AutoComposter] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[AutoComposter] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        // Check if Composter GUI is open
        if (InventoryUtils.isContainerOpen("Composter")) {
            handleComposterGui();
        }
    }

    private void handleComposterGui() {
        // Check organic matter level and fill if needed
        int addOrganicSlot = InventoryUtils.findContainerSlotByName("Add Organic Matter");
        int addFuelSlot = InventoryUtils.findContainerSlotByName("Add Fuel");

        if (addOrganicSlot != -1 && InventoryUtils.hasItem(Items.WHEAT)) {
            mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    addOrganicSlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP,
                    mc.player
            );
            tickDelay = 10;
        } else if (addFuelSlot != -1 && InventoryUtils.hasItem(Items.OAK_LOG)) {
            mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    addFuelSlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP,
                    mc.player
            );
            tickDelay = 10;
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
