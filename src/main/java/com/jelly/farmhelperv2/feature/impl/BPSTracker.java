package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Tracks blocks/crops broken per second during macro farming.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class BPSTracker implements IFeature {

    private static BPSTracker instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    private final Deque<Long> breakTimestamps = new ArrayDeque<>();
    private int currentBPS = 0;
    private BlockPos lastBrokenBlock = null;

    public static BPSTracker getInstance() {
        if (instance == null) instance = new BPSTracker();
        return instance;
    }

    @Override
    public String getName() { return "BPSTracker"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        running = true;
        breakTimestamps.clear();
        currentBPS = 0;
        LogUtils.sendDebug("[BPSTracker] Started.");
    }

    @Override
    public void stop() {
        running = false;
        breakTimestamps.clear();
    }

    @Override
    public void onTick() {
        // Clean up timestamps older than 1 second
        long now = System.currentTimeMillis();
        while (!breakTimestamps.isEmpty() && now - breakTimestamps.peekFirst() > 1000) {
            breakTimestamps.pollFirst();
        }
        currentBPS = breakTimestamps.size();
        MacroHandler.getInstance().setBlocksPerSecond(currentBPS);
    }

    /**
     * Call this whenever a block is broken during farming.
     */
    public void recordBreak(BlockPos pos) {
        breakTimestamps.addLast(System.currentTimeMillis());
        lastBrokenBlock = pos;
        MacroHandler.getInstance().addCropsCollected(1);
    }

    public int getCurrentBPS() { return currentBPS; }
    public BlockPos getLastBrokenBlock() { return lastBrokenBlock; }

    @Override
    public void onWorldLoad() { breakTimestamps.clear(); currentBPS = 0; }
}
