package com.jelly.farmhelperv2.handler;

import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

/**
 * Wrapper around Baritone pathfinding API.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * NOTE: Baritone for Fabric 1.21 must be included as a dependency.
 * This handler uses the Baritone API interfaces.
 * Include in build.gradle:
 *   modImplementation "baritone:baritone-api-fabric:1.21.1-SNAPSHOT"
 *
 * For now, this is a stub implementation that can be extended once
 * Baritone Fabric 1.21 is available.
 */
public class BaritoneHandler {

    private static BaritoneHandler instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private boolean pathfinding = false;
    private BlockPos targetPos = null;

    public static BaritoneHandler getInstance() {
        if (instance == null) instance = new BaritoneHandler();
        return instance;
    }

    /**
     * Navigate to a block position using Baritone.
     * Requires Baritone to be loaded as a dependency.
     */
    public void goTo(BlockPos pos) {
        this.targetPos = pos;
        this.pathfinding = true;
        LogUtils.sendDebug("[BaritoneHandler] Navigating to: " + pos.toShortString());

        // Baritone API call (requires baritone-api-fabric on classpath):
        // BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess()
        //     .setGoalAndPath(new GoalBlock(pos));
        LogUtils.sendMessage("§e[FarmHelper] §fBaritone navigation requested to " + pos.toShortString());
    }

    /**
     * Stop current Baritone pathfinding.
     */
    public void stop() {
        if (!pathfinding) return;
        pathfinding = false;
        targetPos = null;

        // BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
        LogUtils.sendDebug("[BaritoneHandler] Navigation cancelled.");
    }

    /**
     * @return Whether Baritone is currently navigating
     */
    public boolean isPathing() {
        // return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
        return pathfinding;
    }

    public BlockPos getTargetPos() { return targetPos; }
}
