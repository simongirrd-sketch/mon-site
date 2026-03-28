package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * Destroys pests while running a farming macro (on-the-track variant).
 * Kills pests without deviating from the farming path.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class PestsDestroyerOnTheTrack implements IFeature {

    private static PestsDestroyerOnTheTrack instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;
    private static final double ATTACK_RANGE = 4.0;

    public static PestsDestroyerOnTheTrack getInstance() {
        if (instance == null) instance = new PestsDestroyerOnTheTrack();
        return instance;
    }

    @Override
    public String getName() { return "PestsDestroyerOnTheTrack"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInGarden()) {
            LogUtils.sendWarning("[PestsDestroyerOnTheTrack] Not in Garden!");
            return;
        }
        running = true;
        LogUtils.sendMessage("§a[PestsDestroyerOnTheTrack] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[PestsDestroyerOnTheTrack] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        // Only attack pests within melee range (no pathing)
        Box searchBox = mc.player.getBoundingBox().expand(ATTACK_RANGE);
        List<Entity> nearbyPests = mc.world.getOtherEntities(mc.player, searchBox,
                e -> isPestEntity(e) && !e.isRemoved());

        if (!nearbyPests.isEmpty()) {
            Entity closest = nearbyPests.stream()
                    .min((a, b) -> Double.compare(
                            mc.player.squaredDistanceTo(a),
                            mc.player.squaredDistanceTo(b)
                    ))
                    .orElse(null);

            if (closest != null && mc.player.squaredDistanceTo(closest) <= ATTACK_RANGE * ATTACK_RANGE) {
                mc.interactionManager.attackEntity(mc.player, closest);
                mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                tickDelay = 8;
            }
        }
    }

    private boolean isPestEntity(Entity entity) {
        String name = entity.getName().getString();
        return name.contains("Cricket") || name.contains("Locust") ||
                name.contains("Mite") || name.contains("Mosquito") ||
                name.contains("Moth") || name.contains("Rat") ||
                name.contains("Slug") || name.contains("Fly") ||
                name.contains("Worm") || name.contains("Beetle");
    }

    @Override
    public void onWorldLoad() { stop(); }
}
