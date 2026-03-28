package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

/**
 * Reduces game settings for better performance during macro farming.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Key API changes:
 * - GameSettings -> GameOptions
 * - renderDistanceChunks -> viewDistance (SimpleOption)
 * - entityDistance -> entityDistanceScaling
 */
public class PerformanceMode implements IFeature {

    private static PerformanceMode instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    // Saved original settings
    private int savedRenderDistance;
    private int savedMaxFps;
    private double savedEntityDistance;
    private boolean savedParticles;

    public static PerformanceMode getInstance() {
        if (instance == null) instance = new PerformanceMode();
        return instance;
    }

    @Override
    public String getName() { return "PerformanceMode"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (mc.options == null) return;
        GameOptions opts = mc.options;

        // Save original settings
        savedRenderDistance = opts.getViewDistance().getValue();
        savedMaxFps = opts.getMaxFps().getValue();
        savedEntityDistance = opts.getEntityDistanceScaling().getValue();

        // Apply performance settings
        opts.getViewDistance().setValue(4);          // Low render distance
        opts.getMaxFps().setValue(60);               // Cap FPS
        opts.getEntityDistanceScaling().setValue(0.5); // Less entity rendering

        running = true;
        LogUtils.sendMessage("§a[PerformanceMode] §fEnabled. Render distance set to 4.");
    }

    @Override
    public void stop() {
        if (mc.options == null || !running) return;
        GameOptions opts = mc.options;

        // Restore original settings
        opts.getViewDistance().setValue(savedRenderDistance);
        opts.getMaxFps().setValue(savedMaxFps);
        opts.getEntityDistanceScaling().setValue(savedEntityDistance);

        running = false;
        LogUtils.sendMessage("§a[PerformanceMode] §fDisabled. Settings restored.");
    }

    @Override
    public void onTick() { /* No per-tick logic needed */ }
}
