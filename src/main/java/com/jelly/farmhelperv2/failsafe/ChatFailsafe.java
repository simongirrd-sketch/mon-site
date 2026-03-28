package com.jelly.farmhelperv2.failsafe;

import com.jelly.farmhelperv2.FarmHelper;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * Chat and action bar monitoring for failsafe triggers.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Monitors:
 * - Specific chat messages (ban warnings, staff messages)
 * - Action bar for status effects
 * - Nearby players (anti-detection failsafe)
 */
public class ChatFailsafe {

    private static ChatFailsafe instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private int playerCheckTick = 0;

    public static ChatFailsafe getInstance() {
        if (instance == null) instance = new ChatFailsafe();
        return instance;
    }

    /**
     * Called by MixinInGameHud when a chat message is received.
     */
    public void onChatMessage(String message) {
        if (!MacroHandler.getInstance().isMacroEnabled()) return;

        // Check for configured stop keyword
        String keyword = FarmHelper.config.stopChatKeyword;
        if (FarmHelper.config.stopOnChatMessage && !keyword.isEmpty()) {
            if (message.contains(keyword)) {
                MacroHandler.getInstance().stopMacro();
                LogUtils.sendWarning("[Failsafe] Stopping macro - chat keyword detected: " + keyword);
                return;
            }
        }

        // Check for common Hypixel ban/warning messages
        if (message.contains("You have been banned") ||
                message.contains("You are banned") ||
                message.contains("WATCHDOG CHEAT DETECTION")) {
            MacroHandler.getInstance().stopMacro();
            LogUtils.sendError("[Failsafe] WATCHDOG/BAN detected! Macro stopped.");
        }

        // Check for staff messages
        if (message.contains("[ADMIN]") || message.contains("[MOD]")) {
            MacroHandler.getInstance().pauseMacro();
            LogUtils.sendWarning("[Failsafe] Staff member detected in chat! Pausing macro.");
        }
    }

    /**
     * Called by MixinInGameHud when an action bar message is received.
     */
    public void onActionBar(String message) {
        // Monitor Skyblock status from action bar
        // e.g. "Farming Fortune: 1000 ✦"
        // Can be extended to track speed, fortune, etc.
    }

    /**
     * Called every tick to check for nearby players.
     */
    public void onTick() {
        if (!MacroHandler.getInstance().isMacroEnabled()) return;
        if (!FarmHelper.config.stopOnPlayerNearby) return;
        if (mc.player == null || mc.world == null) return;

        playerCheckTick++;
        if (playerCheckTick % 20 != 0) return; // Check once per second

        double radius = FarmHelper.config.playerNearbyRadius;
        Box searchBox = mc.player.getBoundingBox().expand(radius);

        List<PlayerEntity> nearbyPlayers = mc.world.getEntitiesByClass(
                PlayerEntity.class, searchBox,
                p -> !p.equals(mc.player) && !p.isSpectator()
        );

        if (!nearbyPlayers.isEmpty()) {
            PlayerEntity nearest = nearbyPlayers.get(0);
            MacroHandler.getInstance().pauseMacro();
            LogUtils.sendWarning("[Failsafe] Player nearby! Pausing macro. Player: §e" +
                    nearest.getName().getString());
        }
    }
}
