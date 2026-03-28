package com.jelly.farmhelperv2.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Handles smooth player rotation for macros.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * In Fabric 1.21, player rotation is modified via the player entity directly.
 * For anti-cheat-safe rotation, we use interpolation and spread over multiple ticks.
 */
public class RotationHandler {

    private static RotationHandler instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private boolean rotating = false;
    private float targetYaw = 0;
    private float targetPitch = 0;
    private float startYaw = 0;
    private float startPitch = 0;
    private int rotationTicks = 0;
    private int totalRotationTicks = 0;

    // GCD fix for bypassing mouse acceleration detection
    private float lastSentYaw = 0;
    private float lastSentPitch = 0;

    public static RotationHandler getInstance() {
        if (instance == null) instance = new RotationHandler();
        return instance;
    }

    /**
     * Rotate to target yaw/pitch over a number of ticks (smooth rotation).
     */
    public void rotateTo(float yaw, float pitch, int ticks) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        this.targetYaw = yaw;
        this.targetPitch = MathHelper.clamp(pitch, -90f, 90f);
        this.startYaw = player.getYaw();
        this.startPitch = player.getPitch();
        this.rotationTicks = 0;
        this.totalRotationTicks = Math.max(1, ticks);
        this.rotating = true;
    }

    /**
     * Rotate instantly to target yaw/pitch.
     */
    public void rotateInstant(float yaw, float pitch) {
        rotateTo(yaw, pitch, 1);
    }

    /**
     * Called every tick from the main tick handler.
     */
    public void onTick() {
        if (!rotating) return;
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            rotating = false;
            return;
        }

        rotationTicks++;
        float progress = (float) rotationTicks / totalRotationTicks;
        progress = MathHelper.clamp(progress, 0f, 1f);

        // Smooth interpolation (ease in-out)
        float smoothProgress = progress * progress * (3 - 2 * progress);

        float newYaw = interpolateAngle(startYaw, targetYaw, smoothProgress);
        float newPitch = startPitch + (targetPitch - startPitch) * smoothProgress;

        // Apply GCD fix to simulate real mouse movement
        newYaw = applyGCD(newYaw, lastSentYaw);
        newPitch = applyGCD(newPitch, lastSentPitch);

        player.setYaw(newYaw);
        player.setPitch(MathHelper.clamp(newPitch, -90f, 90f));

        lastSentYaw = newYaw;
        lastSentPitch = newPitch;

        if (rotationTicks >= totalRotationTicks) {
            rotating = false;
        }
    }

    private float interpolateAngle(float from, float to, float progress) {
        float delta = to - from;
        // Wrap to [-180, 180]
        while (delta > 180) delta -= 360;
        while (delta < -180) delta += 360;
        return from + delta * progress;
    }

    /**
     * GCD fix: simulate mouse sensitivity steps to avoid detection by anti-cheat.
     * The GCD (Greatest Common Divisor) of mouse movements reveals sensitivity settings,
     * so we need to round to these steps.
     */
    private float applyGCD(float current, float last) {
        float delta = current - last;
        // Simulate typical GCD for 1.0 sensitivity (adjust as needed)
        float gcd = 0.15f;
        delta = Math.round(delta / gcd) * gcd;
        return last + delta;
    }

    public boolean isRotating() { return rotating; }
    public float getTargetYaw() { return targetYaw; }
    public float getTargetPitch() { return targetPitch; }

    public void stop() {
        rotating = false;
    }
}
