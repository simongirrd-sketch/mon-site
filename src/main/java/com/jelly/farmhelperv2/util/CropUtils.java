package com.jelly.farmhelperv2.util;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * Crop-related utility methods for Hypixel Skyblock farming.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class CropUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Crop types supported by FarmHelper.
     */
    public enum CropType {
        WHEAT, CARROT, POTATO, NETHER_WART, SUGARCANE, CACTUS,
        MELON, PUMPKIN, COCOA_BEANS, MUSHROOM
    }

    /** Maps CropType to its Minecraft Block. */
    public static final Map<CropType, Block> CROP_BLOCKS = new HashMap<>();
    /** Maps CropType to its harvest item (drop). */
    public static final Map<CropType, Item> CROP_ITEMS = new HashMap<>();

    static {
        CROP_BLOCKS.put(CropType.WHEAT, Blocks.WHEAT);
        CROP_BLOCKS.put(CropType.CARROT, Blocks.CARROTS);
        CROP_BLOCKS.put(CropType.POTATO, Blocks.POTATOES);
        CROP_BLOCKS.put(CropType.NETHER_WART, Blocks.NETHER_WART);
        CROP_BLOCKS.put(CropType.SUGARCANE, Blocks.SUGAR_CANE);
        CROP_BLOCKS.put(CropType.CACTUS, Blocks.CACTUS);
        CROP_BLOCKS.put(CropType.MELON, Blocks.MELON);
        CROP_BLOCKS.put(CropType.PUMPKIN, Blocks.PUMPKIN);
        CROP_BLOCKS.put(CropType.COCOA_BEANS, Blocks.COCOA);
        CROP_BLOCKS.put(CropType.MUSHROOM, Blocks.RED_MUSHROOM);

        CROP_ITEMS.put(CropType.WHEAT, Items.WHEAT);
        CROP_ITEMS.put(CropType.CARROT, Items.CARROT);
        CROP_ITEMS.put(CropType.POTATO, Items.POTATO);
        CROP_ITEMS.put(CropType.NETHER_WART, Items.NETHER_WART);
        CROP_ITEMS.put(CropType.SUGARCANE, Items.SUGAR_CANE);
        CROP_ITEMS.put(CropType.CACTUS, Items.CACTUS);
        CROP_ITEMS.put(CropType.MELON, Items.MELON_SLICE);
        CROP_ITEMS.put(CropType.PUMPKIN, Items.PUMPKIN);
        CROP_ITEMS.put(CropType.COCOA_BEANS, Items.COCOA_BEANS);
        CROP_ITEMS.put(CropType.MUSHROOM, Items.RED_MUSHROOM);
    }

    /**
     * Check if a block at a position is a fully grown crop.
     */
    public static boolean isFullyGrown(BlockPos pos) {
        if (mc.world == null) return false;
        BlockState state = mc.world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof CropBlock crop) {
            return state.get(crop.getAgeProperty()) >= crop.getMaxAge();
        }
        if (block instanceof NetherWartBlock wart) {
            return state.get(NetherWartBlock.AGE) == 3;
        }
        if (block instanceof CocoaBlock cocoa) {
            return state.get(CocoaBlock.AGE) == 2;
        }
        // Sugar cane, cactus, mushroom don't have growth stages in the same way
        return block == Blocks.SUGAR_CANE || block == Blocks.CACTUS ||
                block == Blocks.RED_MUSHROOM || block == Blocks.BROWN_MUSHROOM ||
                block == Blocks.MELON || block == Blocks.PUMPKIN;
    }

    /**
     * Get the CropType at a block position.
     */
    public static CropType getCropType(BlockPos pos) {
        if (mc.world == null) return null;
        Block block = mc.world.getBlockState(pos).getBlock();
        for (Map.Entry<CropType, Block> entry : CROP_BLOCKS.entrySet()) {
            if (entry.getValue() == block) return entry.getKey();
        }
        return null;
    }

    /**
     * Get the Hypixel Skyblock item ID for a crop type.
     */
    public static String getHypixelItemId(CropType type) {
        return switch (type) {
            case WHEAT -> "ENCHANTED_HAY_BALE";
            case CARROT -> "ENCHANTED_CARROT";
            case POTATO -> "ENCHANTED_POTATO";
            case NETHER_WART -> "ENCHANTED_NETHER_WART";
            case SUGARCANE -> "SUGAR_CANE";
            case CACTUS -> "CACTUS";
            case MELON -> "MELON";
            case PUMPKIN -> "PUMPKIN";
            case COCOA_BEANS -> "COCOA_BEANS";
            case MUSHROOM -> "MUSHROOM_COLLECTION";
        };
    }
}
