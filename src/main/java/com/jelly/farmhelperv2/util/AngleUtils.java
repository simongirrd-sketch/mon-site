package com.jelly.farmhelperv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Angle calculation utilities for player rotation.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class AngleUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Calculate the yaw needed to look at a specific position from the player.
     */
    public static float getYawToPos(Vec3d from, Vec3d to) {
        double diffX = to.x - from.x;
        double diffZ = to.z - from.z;
        double yaw = Math.toDegrees(Math.atan2(diffZ, diffX)) - 90;
        return (float) MathHelper.wrapDegrees(yaw);
    }

    /**
     * Calculate the pitch needed to look at a specific position from the player.
     */
    public static float getPitchToPos(Vec3d from, Vec3d to) {
        double diffX = to.x - from.x;
        double diffY = to.y - from.y;
        double diffZ = to.z - from.z;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        return (float) -Math.toDegrees(Math.atan2(diffY, dist));
    }

    /**
     * Get the yaw and pitch to look at a BlockPos center from the player.
     */
    public static float[] getAnglesTo(BlockPos target) {
        if (mc.player == null) return new float[]{0, 0};
        Vec3d playerEyes = mc.player.getEyePos();
        Vec3d targetVec = new Vec3d(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        return new float[]{
                getYawToPos(playerEyes, targetVec),
                getPitchToPos(playerEyes, targetVec)
        };
    }

    /**
     * Get the yaw and pitch to look at a Vec3d position from the player.
     */
    public static float[] getAnglesTo(Vec3d target) {
        if (mc.player == null) return new float[]{0, 0};
        Vec3d playerEyes = mc.player.getEyePos();
        return new float[]{
                getYawToPos(playerEyes, target),
                getPitchToPos(playerEyes, target)
        };
    }

    /**
     * Calculate the angular difference between two yaw values, wrapped to [-180, 180].
     */
    public static float getYawDifference(float yaw1, float yaw2) {
        float diff = yaw2 - yaw1;
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;
        return diff;
    }

    /**
     * Wrap a yaw value to [-180, 180].
     */
    public static float wrapYaw(float yaw) {
        return MathHelper.wrapDegrees(yaw);
    }

    /**
     * Clamp pitch to the valid range [-90, 90].
     */
    public static float clampPitch(float pitch) {
        return MathHelper.clamp(pitch, -90f, 90f);
    }

    /**
     * Check if the player is approximately facing a direction (within tolerance degrees).
     */
    public static boolean isFacing(float targetYaw, float targetPitch, float yawTolerance, float pitchTolerance) {
        if (mc.player == null) return false;
        float yawDiff = Math.abs(getYawDifference(mc.player.getYaw(), targetYaw));
        float pitchDiff = Math.abs(mc.player.getPitch() - targetPitch);
        return yawDiff <= yawTolerance && pitchDiff <= pitchTolerance;
    }
}
