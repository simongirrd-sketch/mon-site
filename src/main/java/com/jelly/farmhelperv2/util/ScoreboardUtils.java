package com.jelly.farmhelperv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Scoreboard utility methods for reading Hypixel Skyblock data.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Key API changes:
 * - ScoreObjective -> ScoreboardObjective
 * - scoreboard.getScoreObjective() -> scoreboard.getNullableObjective()
 * - Score.getPlayerName() -> ScoreboardEntry.owner()
 */
public class ScoreboardUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Get the title of the sidebar scoreboard (e.g. "SKYBLOCK" header on Hypixel).
     */
    public static String getScoreboardTitle() {
        if (mc.world == null) return "";
        Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(
                net.minecraft.scoreboard.ScoreboardDisplaySlot.SIDEBAR
        );
        if (sidebar == null) return "";
        Text displayName = sidebar.getDisplayName();
        return displayName != null ? net.minecraft.util.Formatting.strip(displayName.getString()) : "";
    }

    /**
     * Get all lines from the sidebar scoreboard.
     */
    public static List<String> getScoreboardLines() {
        List<String> lines = new ArrayList<>();
        if (mc.world == null) return lines;

        Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(
                net.minecraft.scoreboard.ScoreboardDisplaySlot.SIDEBAR
        );
        if (sidebar == null) return lines;

        Collection<ScoreboardEntry> scores = scoreboard.getScoreboardEntries(sidebar);
        for (ScoreboardEntry entry : scores) {
            String name = entry.owner();
            Team team = scoreboard.getScoreHolderTeam(name);
            if (team != null) {
                name = team.getPrefix().getString() + name + team.getSuffix().getString();
            }
            String clean = net.minecraft.util.Formatting.strip(name);
            if (clean != null && !clean.isEmpty()) {
                lines.add(clean);
            }
        }
        return lines;
    }

    /**
     * Get the current location from the scoreboard (Hypixel Skyblock sidebar).
     */
    public static String getLocationFromScoreboard() {
        List<String> lines = getScoreboardLines();
        for (String line : lines) {
            if (line.contains("⏣") || line.contains("ф")) {
                return line.replace("⏣", "").replace("ф", "").trim();
            }
        }
        return "";
    }

    /**
     * Check if a specific string is present in the scoreboard.
     */
    public static boolean scoreboardContains(String text) {
        return getScoreboardLines().stream().anyMatch(line -> line.contains(text));
    }

    /**
     * Get a specific scoreboard line containing a keyword.
     */
    public static String getLineContaining(String keyword) {
        return getScoreboardLines().stream()
                .filter(line -> line.contains(keyword))
                .findFirst()
                .orElse("");
    }
}
