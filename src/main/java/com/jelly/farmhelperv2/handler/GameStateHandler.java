package com.jelly.farmhelperv2.handler;

import com.jelly.farmhelperv2.util.LogUtils;
import com.jelly.farmhelperv2.util.ScoreboardUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Tracks game state for Hypixel Skyblock.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class GameStateHandler {

    private static GameStateHandler instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Game state flags
    private boolean inHypixel = false;
    private boolean inSkyblock = false;
    private boolean inGarden = false;
    private boolean inFarm = false;
    private String currentLocation = "";
    private String currentIsland = "";
    private int tickCounter = 0;

    // Player state
    private double playerX, playerY, playerZ;
    private float playerYaw, playerPitch;

    public static GameStateHandler getInstance() {
        if (instance == null) instance = new GameStateHandler();
        return instance;
    }

    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        tickCounter++;

        // Update player position cache
        playerX = player.getX();
        playerY = player.getY();
        playerZ = player.getZ();
        playerYaw = player.getYaw();
        playerPitch = player.getPitch();

        // Check server every 20 ticks (1 second)
        if (tickCounter % 20 == 0) {
            updateServerInfo();
        }
    }

    private void updateServerInfo() {
        if (mc.getCurrentServerEntry() == null) {
            inHypixel = false;
            inSkyblock = false;
            inGarden = false;
            return;
        }

        String serverAddress = mc.getCurrentServerEntry().address.toLowerCase();
        inHypixel = serverAddress.contains("hypixel.net");

        if (inHypixel) {
            String scoreboard = ScoreboardUtils.getScoreboardTitle();
            inSkyblock = scoreboard != null && scoreboard.contains("SKYBLOCK");

            // Check current location from scoreboard sidebar
            currentLocation = ScoreboardUtils.getLocationFromScoreboard();
            inGarden = currentLocation.contains("Garden");
        }
    }

    public void onWorldLoad() {
        tickCounter = 0;
        LogUtils.sendDebug("[GameStateHandler] World loaded.");
    }

    public void onWorldUnload() {
        inHypixel = false;
        inSkyblock = false;
        inGarden = false;
        inFarm = false;
        currentLocation = "";
        currentIsland = "";
        LogUtils.sendDebug("[GameStateHandler] World unloaded.");
    }

    // Getters
    public boolean isInHypixel() { return inHypixel; }
    public boolean isInSkyblock() { return inSkyblock; }
    public boolean isInGarden() { return inGarden; }
    public boolean isInFarm() { return inFarm; }
    public String getCurrentLocation() { return currentLocation; }
    public String getCurrentIsland() { return currentIsland; }
    public double getPlayerX() { return playerX; }
    public double getPlayerY() { return playerY; }
    public double getPlayerZ() { return playerZ; }
    public float getPlayerYaw() { return playerYaw; }
    public float getPlayerPitch() { return playerPitch; }
}
