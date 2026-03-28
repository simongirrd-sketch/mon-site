package com.jelly.farmhelperv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab list (player list) utility methods for reading Hypixel Skyblock data.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Key API changes:
 * - NetworkPlayerInfo -> PlayerListEntry
 * - getPlayerInfo() -> getPlayerListEntries()
 * - getGameProfile().getName() -> getProfile().getName()
 */
public class TablistUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Get all tab list entries as display strings (includes Hypixel formatting).
     */
    public static List<String> getTablistEntries() {
        List<String> entries = new ArrayList<>();
        if (mc.player == null || mc.getNetworkHandler() == null) return entries;

        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
            if (entry.getDisplayName() != null) {
                String display = entry.getDisplayName().getString();
                String clean = Formatting.strip(display);
                if (clean != null && !clean.isEmpty()) {
                    entries.add(clean);
                }
            } else {
                entries.add(entry.getProfile().getName());
            }
        }
        return entries;
    }

    /**
     * Get the raw (colored) tab list entries.
     */
    public static List<String> getTablistEntriesRaw() {
        List<String> entries = new ArrayList<>();
        if (mc.player == null || mc.getNetworkHandler() == null) return entries;

        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
            if (entry.getDisplayName() != null) {
                entries.add(entry.getDisplayName().getString());
            }
        }
        return entries;
    }

    /**
     * Find a tab list entry containing a keyword.
     */
    public static String getEntryContaining(String keyword) {
        return getTablistEntries().stream()
                .filter(entry -> entry.contains(keyword))
                .findFirst()
                .orElse("");
    }

    /**
     * Check if the tab list contains a specific string.
     */
    public static boolean tablistContains(String text) {
        return getTablistEntries().stream().anyMatch(entry -> entry.contains(text));
    }

    /**
     * Get the server type from the tab list (SkyBlock, BedWars, etc.).
     */
    public static String getGameMode() {
        String entry = getEntryContaining("Mode:");
        if (entry.isEmpty()) return "Unknown";
        int index = entry.indexOf("Mode:") + 5;
        return entry.substring(index).trim();
    }
}
