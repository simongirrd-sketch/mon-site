package com.jelly.farmhelperv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Player utility methods.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Key API changes:
 * - EntityPlayerSP -> ClientPlayerEntity
 * - player.posX/posY/posZ -> player.getX()/getY()/getZ()
 * - player.rotationYaw/rotationPitch -> player.getYaw()/getPitch()
 * - player.inventory.getCurrentItem() -> player.getMainHandStack()
 * - player.isPotionActive() -> player.hasStatusEffect()
 */
public class PlayerUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static ClientPlayerEntity getPlayer() {
        return mc.player;
    }

    public static boolean isPlayerNull() {
        return mc.player == null;
    }

    public static Vec3d getPlayerPos() {
        if (mc.player == null) return Vec3d.ZERO;
        return mc.player.getPos();
    }

    public static BlockPos getPlayerBlockPos() {
        if (mc.player == null) return BlockPos.ORIGIN;
        return mc.player.getBlockPos();
    }

    public static float getPlayerYaw() {
        if (mc.player == null) return 0;
        return mc.player.getYaw();
    }

    public static float getPlayerPitch() {
        if (mc.player == null) return 0;
        return mc.player.getPitch();
    }

    public static ItemStack getHeldItem() {
        if (mc.player == null) return ItemStack.EMPTY;
        return mc.player.getMainHandStack();
    }

    public static ItemStack getOffhandItem() {
        if (mc.player == null) return ItemStack.EMPTY;
        return mc.player.getOffHandStack();
    }

    public static boolean hasEffect(net.minecraft.entity.effect.StatusEffect effect) {
        if (mc.player == null) return false;
        return mc.player.hasStatusEffect(mc.world.getRegistryManager()
                .get(net.minecraft.registry.RegistryKeys.STATUS_EFFECT)
                .getEntry(effect).orElseThrow());
    }

    public static boolean isMoving() {
        if (mc.player == null) return false;
        return mc.player.input != null &&
                (mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0);
    }

    public static boolean isOnGround() {
        if (mc.player == null) return false;
        return mc.player.isOnGround();
    }

    public static boolean isInWater() {
        if (mc.player == null) return false;
        return mc.player.isTouchingWater();
    }

    public static double getDistanceTo(BlockPos pos) {
        if (mc.player == null) return Double.MAX_VALUE;
        return mc.player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static double getDistanceTo(Vec3d pos) {
        if (mc.player == null) return Double.MAX_VALUE;
        return mc.player.distanceTo(
                new net.minecraft.entity.Entity(null, mc.world) {{
                    setPosition(pos);
                }}
        );
    }

    /**
     * Get the player's horizontal speed in blocks per second.
     */
    public static double getHorizontalSpeed() {
        if (mc.player == null) return 0;
        Vec3d vel = mc.player.getVelocity();
        return Math.sqrt(vel.x * vel.x + vel.z * vel.z) * 20; // *20 for per-second
    }

    /**
     * Simulate key press via input options.
     * In Fabric 1.21, use KeyBinding.setKeyPressed or direct input manipulation.
     */
    public static void setKeyPressed(net.minecraft.client.option.KeyBinding key, boolean pressed) {
        net.minecraft.client.option.KeyBinding.setKeyPressed(key.getDefaultKey(), pressed);
    }
}
