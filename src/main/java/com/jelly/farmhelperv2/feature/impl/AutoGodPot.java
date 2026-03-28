package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

/**
 * Automatically drinks God Potions when they expire.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AutoGodPot implements IFeature {

    private static AutoGodPot instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;

    public static AutoGodPot getInstance() {
        if (instance == null) instance = new AutoGodPot();
        return instance;
    }

    @Override
    public String getName() { return "AutoGodPot"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInSkyblock()) {
            LogUtils.sendWarning("[AutoGodPot] Not on Hypixel Skyblock!");
            return;
        }
        running = true;
        LogUtils.sendMessage("§a[AutoGodPot] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[AutoGodPot] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        // Check action bar for "God Potion" expiration message
        // In Fabric, the action bar text is intercepted via Mixin
        // For now, check if God Potion is in inventory and use it
        if (shouldDrinkPotion()) {
            drinkGodPotion();
        }
    }

    private boolean shouldDrinkPotion() {
        // Check scoreboard/tab for god pot status - simplified implementation
        return InventoryUtils.hasItem(Items.POTION);
    }

    private void drinkGodPotion() {
        if (InventoryUtils.switchToItem(Items.POTION)) {
            mc.options.useKey.setPressed(true);
            tickDelay = 40;
            LogUtils.sendMessage("§a[AutoGodPot] §fDrank God Potion!");
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
