package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.BaritoneHandler;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * Automatically farms pests in the Garden for loot.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Key API changes:
 * - world.getEntitiesWithinAABB() -> world.getEntitiesByClass()
 * - Entity.getEntityBoundingBox() -> entity.getBoundingBox()
 */
public class PestFarmer implements IFeature {

    private static PestFarmer instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;
    private Entity targetPest = null;

    public static PestFarmer getInstance() {
        if (instance == null) instance = new PestFarmer();
        return instance;
    }

    @Override
    public String getName() { return "PestFarmer"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInGarden()) {
            LogUtils.sendWarning("[PestFarmer] Not in Garden!");
            return;
        }
        running = true;
        targetPest = null;
        LogUtils.sendMessage("§a[PestFarmer] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        targetPest = null;
        BaritoneHandler.getInstance().stop();
        LogUtils.sendMessage("§a[PestFarmer] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        // Find nearest pest entity
        findNearestPest();

        if (targetPest != null && targetPest.isAlive()) {
            attackPest();
        } else {
            targetPest = null;
        }
    }

    private void findNearestPest() {
        if (mc.world == null) return;

        Box searchBox = mc.player.getBoundingBox().expand(20);
        // Pests in Hypixel Skyblock are represented as special entities
        // They typically appear as slimes, silverfish, etc.
        List<Entity> entities = mc.world.getOtherEntities(mc.player, searchBox,
                e -> e instanceof SlimeEntity && !e.isRemoved());

        if (entities.isEmpty()) {
            targetPest = null;
            return;
        }

        // Find closest pest
        targetPest = entities.stream()
                .min((a, b) -> Double.compare(
                        mc.player.squaredDistanceTo(a),
                        mc.player.squaredDistanceTo(b)
                ))
                .orElse(null);
    }

    private void attackPest() {
        if (targetPest == null) return;

        double dist = mc.player.squaredDistanceTo(targetPest);
        if (dist <= 16) { // Within 4 blocks
            mc.interactionManager.attackEntity(mc.player, targetPest);
            mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            tickDelay = 10;
        } else {
            // Navigate to pest using Baritone
            BlockPos pestPos = targetPest.getBlockPos();
            if (!BaritoneHandler.getInstance().isPathing()) {
                BaritoneHandler.getInstance().goTo(pestPos);
            }
        }
    }

    @Override
    public void onWorldLoad() { stop(); }
}
