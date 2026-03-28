package com.jelly.farmhelperv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory utility methods.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Key API changes:
 * - player.inventory -> player.getInventory()
 * - GuiChest -> GenericContainerScreen
 * - slot.getStack() is the same
 * - Item registration: Items.DIAMOND vs new Item()
 */
public class InventoryUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Get the player's hotbar slot for a specific item type.
     * Returns -1 if not found.
     */
    public static int getHotbarSlot(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Switch to a hotbar slot containing the specified item.
     * Returns true if successful.
     */
    public static boolean switchToItem(Item item) {
        int slot = getHotbarSlot(item);
        if (slot == -1) return false;
        mc.player.getInventory().selectedSlot = slot;
        return true;
    }

    /**
     * Get all stacks in the player's inventory containing an item.
     */
    public static List<ItemStack> getItemsInInventory(Item item) {
        List<ItemStack> result = new ArrayList<>();
        if (mc.player == null) return result;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                result.add(stack);
            }
        }
        return result;
    }

    /**
     * Count items of a type in the player's inventory.
     */
    public static int countItem(Item item) {
        return getItemsInInventory(item).stream().mapToInt(ItemStack::getCount).sum();
    }

    /**
     * Check if inventory contains an item.
     */
    public static boolean hasItem(Item item) {
        return getHotbarSlot(item) != -1 || countItem(item) > 0;
    }

    /**
     * Check if a chest/container GUI is open and get its title.
     */
    public static String getOpenContainerTitle() {
        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            Text title = screen.getTitle();
            return title != null ? title.getString() : "";
        }
        return "";
    }

    /**
     * Check if a container with a specific title is open.
     */
    public static boolean isContainerOpen(String titleContains) {
        return getOpenContainerTitle().contains(titleContains);
    }

    /**
     * Get a slot in the open container by index.
     */
    public static ItemStack getContainerSlot(int index) {
        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            List<Slot> slots = screen.getScreenHandler().slots;
            if (index >= 0 && index < slots.size()) {
                return slots.get(index).getStack();
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Find a slot in the open container by item display name (partial match).
     * Returns -1 if not found.
     */
    public static int findContainerSlotByName(String namePart) {
        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            List<Slot> slots = screen.getScreenHandler().slots;
            for (int i = 0; i < slots.size(); i++) {
                ItemStack stack = slots.get(i).getStack();
                if (!stack.isEmpty()) {
                    String name = stack.getName().getString();
                    if (name.contains(namePart)) return i;
                }
            }
        }
        return -1;
    }
}
