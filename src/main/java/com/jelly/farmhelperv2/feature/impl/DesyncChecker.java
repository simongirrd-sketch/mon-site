package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

/**
 * Detects player position desyncs and corrects them.
 * A desync happens when the client position diverges from server position.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class DesyncChecker implements IFeature {

    private static DesyncChecker instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    private Vec3d lastKnownPos = null;
    private Vec3d expectedPos = null;
    private int stuckTicks = 0;
    private static final int STUCK_THRESHOLD = 60; // 3 seconds of being stuck
    private static final double DESYNC_THRESHOLD = 5.0;

    public static DesyncChecker getInstance() {
        if (instance == null) instance = new DesyncChecker();
        return instance;
    }

    @Override
    public String getName() { return "DesyncChecker"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        running = true;
        stuckTicks = 0;
        lastKnownPos = mc.player != null ? mc.player.getPos() : null;
        LogUtils.sendDebug("[DesyncChecker] Started.");
    }

    @Override
    public void stop() {
        running = false;
        stuckTicks = 0;
    }

    @Override
    public void onTick() {
        if (mc.player == null || !MacroHandler.getInstance().isMacroEnabled()) return;

        Vec3d currentPos = mc.player.getPos();

        if (lastKnownPos != null) {
            double moved = currentPos.distanceTo(lastKnownPos);

            // If macro is running but player hasn't moved
            if (moved < 0.01 && MacroHandler.getInstance().isMacroEnabled()) {
                stuckTicks++;
                if (stuckTicks >= STUCK_THRESHOLD) {
                    onDesyncDetected(currentPos);
                }
            } else {
                stuckTicks = 0;
            }
        }

        lastKnownPos = currentPos;
    }

    private void onDesyncDetected(Vec3d pos) {
        stuckTicks = 0;
        LogUtils.sendWarning("[DesyncChecker] Desync/stuck detected at " +
                String.format("%.1f %.1f %.1f", pos.x, pos.y, pos.z));

        // Attempt to resync by sending a position packet
        // In Fabric, this is done automatically by the client
        // We can try jumping to break the stuck state
        if (mc.player != null) {
            mc.player.jump();
        }

        // If still stuck after multiple attempts, pause macro
        MacroHandler.getInstance().pauseMacro();
    }

    public int getStuckTicks() { return stuckTicks; }

    @Override
    public void onWorldLoad() {
        stuckTicks = 0;
        lastKnownPos = null;
    }
}
