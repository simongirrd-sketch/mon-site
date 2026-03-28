package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

/**
 * Automatically uses pest repellent to prevent pest infestations.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AutoRepellent implements IFeature {

    private static AutoRepellent instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;

    public static AutoRepellent getInstance() {
        if (instance == null) instance = new AutoRepellent();
        return instance;
    }

    @Override
    public String getName() { return "AutoRepellent"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInGarden()) {
            LogUtils.sendWarning("[AutoRepellent] Not in Garden!");
            return;
        }
        running = true;
        LogUtils.sendMessage("§a[AutoRepellent] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[AutoRepellent] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (tickDelay > 0) { tickDelay--; return; }
        // Use repellent when pests are detected (simplified: use on a timer)
        // Real implementation would check scoreboard for pest count
        if (InventoryUtils.switchToItem(Items.GLASS_BOTTLE)) {
            mc.options.useKey.setPressed(true);
            tickDelay = 400; // Use every 20 seconds
            LogUtils.sendMessage("§a[AutoRepellent] §fUsed repellent.");
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
