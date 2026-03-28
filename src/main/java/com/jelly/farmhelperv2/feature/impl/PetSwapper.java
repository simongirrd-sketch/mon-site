package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Automatically swaps pets based on current activity (farming vs other).
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class PetSwapper implements IFeature {

    private static PetSwapper instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;
    private String farmingPetName = "Elephant";
    private String otherPetName = "Tiger";

    public static PetSwapper getInstance() {
        if (instance == null) instance = new PetSwapper();
        return instance;
    }

    public void setFarmingPetName(String name) { this.farmingPetName = name; }
    public void setOtherPetName(String name) { this.otherPetName = name; }

    @Override
    public String getName() { return "PetSwapper"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInSkyblock()) {
            LogUtils.sendWarning("[PetSwapper] Not on Hypixel Skyblock!");
            return;
        }
        running = true;
        tickDelay = 0;
        LogUtils.sendMessage("§a[PetSwapper] §fStarted. Farming pet: §e" + farmingPetName);
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[PetSwapper] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        if (InventoryUtils.isContainerOpen("Pets")) {
            handlePetsGui();
        }
    }

    private void handlePetsGui() {
        String targetPet = GameStateHandler.getInstance().isInGarden() ? farmingPetName : otherPetName;
        int petSlot = InventoryUtils.findContainerSlotByName(targetPet);

        if (petSlot != -1) {
            mc.interactionManager.clickSlot(
                    mc.player.currentScreenHandler.syncId,
                    petSlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP,
                    mc.player
            );
            tickDelay = 20;
            stop(); // One-shot operation
            LogUtils.sendMessage("§a[PetSwapper] §fEquipped §e" + targetPet + " §fpet!");
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
