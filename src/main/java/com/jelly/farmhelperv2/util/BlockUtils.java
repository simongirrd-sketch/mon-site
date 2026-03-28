package com.jelly.farmhelperv2.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Block utility methods.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Key API changes:
 * - world.getBlockState(pos).getBlock() replaces world.getBlock(x, y, z)
 * - BlockPos replaces manual x/y/z coordinates
 * - Block.getIdFromBlock() -> Registry.BLOCK.getId()
 */
public class BlockUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static World getWorld() {
        return mc.world;
    }

    public static BlockState getBlockState(BlockPos pos) {
        if (mc.world == null) return Blocks.AIR.getDefaultState();
        return mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getBlockState(pos).getBlock();
    }

    public static boolean isAir(BlockPos pos) {
        return getBlock(pos) == Blocks.AIR;
    }

    public static boolean isSolid(BlockPos pos) {
        return getBlockState(pos).isSolidBlock(mc.world, pos);
    }

    /**
     * Get all blocks of a specific type within a radius.
     */
    public static List<BlockPos> getBlocksInRadius(Block targetBlock, int radius) {
        List<BlockPos> result = new ArrayList<>();
        if (mc.player == null || mc.world == null) return result;

        BlockPos center = mc.player.getBlockPos();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    if (getBlock(pos) == targetBlock) {
                        result.add(pos);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Get the closest block of a type to the player.
     */
    public static BlockPos getClosestBlock(Block targetBlock, int radius) {
        if (mc.player == null) return null;
        List<BlockPos> blocks = getBlocksInRadius(targetBlock, radius);
        BlockPos closest = null;
        double minDist = Double.MAX_VALUE;
        for (BlockPos pos : blocks) {
            double dist = mc.player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (dist < minDist) {
                minDist = dist;
                closest = pos;
            }
        }
        return closest;
    }

    /**
     * Get the Vec3d center of a block position.
     */
    public static Vec3d getBlockCenter(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    /**
     * Check if a block position is within the player's reach (default 4.5 blocks in survival).
     */
    public static boolean isInReach(BlockPos pos, double reach) {
        if (mc.player == null) return false;
        double dist = mc.player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        return dist <= reach * reach;
    }

    /**
     * Get the adjacent block positions (6 faces).
     */
    public static List<BlockPos> getAdjacentPositions(BlockPos pos) {
        List<BlockPos> adjacent = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            adjacent.add(pos.offset(dir));
        }
        return adjacent;
    }
}
