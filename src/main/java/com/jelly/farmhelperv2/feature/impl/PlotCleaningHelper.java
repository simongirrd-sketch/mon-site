package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.BlockUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Helps clean up a Garden plot by removing weeds/unwanted blocks.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class PlotCleaningHelper implements IFeature {

    private static PlotCleaningHelper instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;
    private BlockPos targetBlock = null;

    public static PlotCleaningHelper getInstance() {
        if (instance == null) instance = new PlotCleaningHelper();
        return instance;
    }

    @Override
    public String getName() { return "PlotCleaningHelper"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInGarden()) {
            LogUtils.sendWarning("[PlotCleaningHelper] Not in Garden!");
            return;
        }
        running = true;
        LogUtils.sendMessage("§a[PlotCleaningHelper] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        targetBlock = null;
        LogUtils.sendMessage("§a[PlotCleaningHelper] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        // Find weeds (grass, dead bushes, etc.) within reach
        List<BlockPos> weeds = BlockUtils.getBlocksInRadius(Blocks.SHORT_GRASS, 5);
        weeds.addAll(BlockUtils.getBlocksInRadius(Blocks.DEAD_BUSH, 5));

        if (!weeds.isEmpty()) {
            targetBlock = weeds.get(0);
            if (BlockUtils.isInReach(targetBlock, 4.5)) {
                breakBlock(targetBlock);
            }
        } else {
            // No more weeds nearby, stop
            stop();
            LogUtils.sendMessage("§a[PlotCleaningHelper] §fPlot cleaned!");
        }
    }

    private void breakBlock(BlockPos pos) {
        if (mc.interactionManager == null) return;
        net.minecraft.util.math.Direction face = net.minecraft.util.math.Direction.UP;
        mc.interactionManager.updateBlockBreakingProgress(pos, face);
        mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        tickDelay = 5;
    }

    @Override
    public void onWorldLoad() { stop(); }
}
