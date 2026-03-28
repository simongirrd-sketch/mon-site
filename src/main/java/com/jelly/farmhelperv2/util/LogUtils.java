package com.jelly.farmhelperv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging utilities.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class LogUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger("FarmHelperV2");
    private static boolean debugMode = false;

    /**
     * Send a colored message in the in-game chat.
     * Replaces Forge's ChatComponentText.
     */
    public static void sendMessage(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(message), false);
        } else {
            LOGGER.info("[FarmHelper] {}", message);
        }
    }

    /**
     * Send an error message in the in-game chat.
     */
    public static void sendError(String message) {
        sendMessage("§c" + message);
        LOGGER.error("[FarmHelper] {}", message);
    }

    /**
     * Send a warning message in the in-game chat.
     */
    public static void sendWarning(String message) {
        sendMessage("§e" + message);
        LOGGER.warn("[FarmHelper] {}", message);
    }

    /**
     * Send a debug message (only visible when debug mode is on).
     */
    public static void sendDebug(String message) {
        if (debugMode) {
            sendMessage("§7[DEBUG] " + message);
        }
        LOGGER.debug("[FarmHelper] {}", message);
    }

    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }
}
