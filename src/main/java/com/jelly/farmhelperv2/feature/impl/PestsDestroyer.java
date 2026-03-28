package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * Destroys pest entities that appear in the Garden.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class PestsDestroyer implements IFeature {

    private static PestsDestroyer instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;

    public static PestsDestroyer getInstance() {
        if (instance == null) instance = new PestsDestroyer();
        return instance;
    }

    @Override
    public String getName() { return "PestsDestroyer"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInGarden()) {
            LogUtils.sendWarning("[PestsDestroyer] Not in Garden!");
            return;
        }
        running = true;
        LogUtils.sendMessage("§a[PestsDestroyer] §fStarted.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[PestsDestroyer] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        // Find and attack nearby pest entities
        Box searchBox = mc.player.getBoundingBox().expand(5);
        List<Entity> nearbyEntities = mc.world.getOtherEntities(mc.player, searchBox,
                e -> isPestEntity(e) && !e.isRemoved());

        for (Entity pest : nearbyEntities) {
            if (mc.player.squaredDistanceTo(pest) <= 9) { // Within 3 blocks
                mc.interactionManager.attackEntity(mc.player, pest);
                mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                tickDelay = 5;
                break;
            }
        }
    }

    private boolean isPestEntity(Entity entity) {
        // Pests in Skyblock have special names
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
