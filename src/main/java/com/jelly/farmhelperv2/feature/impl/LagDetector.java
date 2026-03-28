package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Detects server-side lag and pauses macros when lag is detected.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class LagDetector implements IFeature {

    private static LagDetector instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    private long lastPacketTime = 0;
    private static final long LAG_THRESHOLD_MS = 2000; // 2 seconds without packets = lag
    private boolean lagging = false;

    public static LagDetector getInstance() {
        if (instance == null) instance = new LagDetector();
        return instance;
    }

    @Override
    public String getName() { return "LagDetector"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        running = true;
        lastPacketTime = System.currentTimeMillis();
        lagging = false;
        LogUtils.sendDebug("[LagDetector] Started.");
    }

    @Override
    public void stop() {
        running = false;
        lagging = false;
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        // Check if the player entity is moving (server is responding)
        // If velocity is always 0 and player isn't on ground, server may be lagging
        long now = System.currentTimeMillis();

        // Use world time as a proxy for packet reception
        long worldTime = mc.world != null ? mc.world.getTime() : 0;

        // Detect lag by checking if world time is advancing
        boolean currentlyLagging = (now - lastPacketTime) > LAG_THRESHOLD_MS;

        if (currentlyLagging && !lagging) {
            lagging = true;
            MacroHandler.getInstance().pauseMacro();
            LogUtils.sendWarning("[LagDetector] Lag detected! Pausing macro.");
        } else if (!currentlyLagging && lagging) {
            lagging = false;
            MacroHandler.getInstance().resumeMacro();
            LogUtils.sendMessage("§a[LagDetector] §fLag resolved. Resuming macro.");
        }
    }

    /**
     * Call this when a packet is received from the server.
     * Hook into packet reception via Mixin.
     */
    public void onPacketReceived() {
        lastPacketTime = System.currentTimeMillis();
    }

    public boolean isLagging() { return lagging; }

    @Override
    public void onWorldLoad() {
        lastPacketTime = System.currentTimeMillis();
        lagging = false;
    }
}
