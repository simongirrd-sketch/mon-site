package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.KeyBindUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

/**
 * Detects when the player is stuck and attempts to unstick them.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AntiStuck implements IFeature {

    private static AntiStuck instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    private Vec3d lastPos = null;
    private int stuckTicks = 0;
    private int unstuckAttempt = 0;
    private static final int STUCK_TICKS = 40; // 2 seconds

    public static AntiStuck getInstance() {
        if (instance == null) instance = new AntiStuck();
        return instance;
    }

    @Override
    public String getName() { return "AntiStuck"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        running = true;
        stuckTicks = 0;
        unstuckAttempt = 0;
        lastPos = mc.player != null ? mc.player.getPos() : null;
        LogUtils.sendDebug("[AntiStuck] Started.");
    }

    @Override
    public void stop() {
        running = false;
        stuckTicks = 0;
        unstuckAttempt = 0;
    }

    @Override
    public void onTick() {
        if (mc.player == null || !MacroHandler.getInstance().isMacroEnabled()) return;

        Vec3d pos = mc.player.getPos();
        if (lastPos == null) { lastPos = pos; return; }

        double moved = new Vec3d(pos.x - lastPos.x, 0, pos.z - lastPos.z).length();

        if (moved < 0.05) {
            stuckTicks++;
            if (stuckTicks >= STUCK_TICKS) {
                attemptUnstuck();
            }
        } else {
            stuckTicks = 0;
            unstuckAttempt = 0;
        }

        lastPos = pos;
    }

    private void attemptUnstuck() {
        stuckTicks = 0;
        unstuckAttempt++;
        LogUtils.sendWarning("[AntiStuck] Player appears stuck! Attempt " + unstuckAttempt);

        switch (unstuckAttempt % 4) {
            case 1 -> { KeyBindUtils.holdJump(true); }
            case 2 -> { KeyBindUtils.holdBack(true); }
            case 3 -> {
                KeyBindUtils.holdLeft(true);
                KeyBindUtils.holdJump(true);
            }
            case 0 -> {
                KeyBindUtils.releaseAllMovementKeys();
                if (unstuckAttempt >= 12) {
                    LogUtils.sendError("[AntiStuck] Cannot unstick player. Stopping macro.");
                    MacroHandler.getInstance().stopMacro();
                    stop();
                }
            }
        }
    }

    @Override
    public void onWorldLoad() {
        stuckTicks = 0;
        unstuckAttempt = 0;
        lastPos = null;
    }
}
