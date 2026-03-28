package com.jelly.farmhelperv2.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jelly.farmhelperv2.util.CropUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

/**
 * Main configuration class for FarmHelper V2.
 * Converted from Forge 1.8.9 Forge Config to a Gson-based JSON config for Fabric 1.21.1.
 *
 * Cloth Config (me.shedaniel.cloth:cloth-config-fabric) is used for the in-game GUI.
 */
public class FarmHelperConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final File CONFIG_FILE = CONFIG_DIR.resolve("farmhelperv2.json").toFile();

    // =====================
    // General Settings
    // =====================
    public boolean debugMode = false;
    public boolean showHUD = true;
    public boolean enableNotifications = true;
    public String apiKey = "";

    // =====================
    // Macro Settings
    // =====================
    public boolean macroEnabled = false;
    public CropUtils.CropType selectedCrop = CropUtils.CropType.WHEAT;
    public int farmingSpeed = 400; // Rancher boots speed
    public boolean enableAntiStuck = true;
    public boolean enableDesyncCheck = true;
    public boolean enableLagDetector = true;
    public int lagThresholdMs = 2000;

    // =====================
    // Feature Toggles
    // =====================
    public boolean autoSellEnabled = false;
    public boolean autoBazaarEnabled = false;
    public boolean autoComposterEnabled = false;
    public boolean autoRepellentEnabled = false;
    public boolean autoSprayonatorEnabled = false;
    public boolean autoGodPotEnabled = false;
    public boolean autoCookieEnabled = false;
    public boolean autoWardrobeEnabled = false;
    public boolean autoReconnectEnabled = true;
    public boolean pestFarmerEnabled = false;
    public boolean pestsDestroyerEnabled = true;
    public boolean pestsDestroyerOnTrackEnabled = true;
    public boolean plotCleaningEnabled = false;
    public boolean visitorsMacroEnabled = false;
    public boolean petSwapperEnabled = false;
    public boolean rancherSpeedSetterEnabled = false;
    public boolean performanceModeEnabled = false;
    public boolean ungrabMouseEnabled = false;
    public boolean freelookEnabled = false;
    public boolean schedulerEnabled = false;
    public boolean profitCalculatorEnabled = true;
    public boolean bpsTrackerEnabled = true;

    // =====================
    // AutoSell / AutoBazaar
    // =====================
    public int sellInventoryFullPercent = 80; // Sell when inventory is X% full
    public boolean sellToNPC = false;         // false = use Bazaar

    // =====================
    // Scheduler
    // =====================
    public String schedulerStartTime = "08:00";
    public String schedulerStopTime = "22:00";
    public boolean schedulerEnabled2 = false;

    // =====================
    // Pet Swapper
    // =====================
    public String farmingPetName = "Elephant";
    public String otherPetName = "Tiger";

    // =====================
    // Leave Timer
    // =====================
    public boolean leaveTimerEnabled = false;
    public int leaveTimerMinutes = 60;

    // =====================
    // HUD Settings
    // =====================
    public int hudX = 10;
    public int hudY = 10;
    public boolean showBPS = true;
    public boolean showProfitPerHour = true;
    public boolean showMacroStatus = true;
    public boolean showRuntime = true;

    // =====================
    // Failsafe Settings
    // =====================
    public boolean stopOnPlayerNearby = true;
    public double playerNearbyRadius = 20.0;
    public boolean stopOnChatMessage = false;
    public String stopChatKeyword = "";

    // =====================
    // Load / Save
    // =====================

    public static FarmHelperConfig load() {
        if (!CONFIG_FILE.exists()) {
            LogUtils.LOGGER.info("[FarmHelper] No config found, creating default config.");
            FarmHelperConfig config = new FarmHelperConfig();
            config.save();
            return config;
        }

        try (Reader reader = new FileReader(CONFIG_FILE)) {
            FarmHelperConfig config = GSON.fromJson(reader, FarmHelperConfig.class);
            if (config == null) config = new FarmHelperConfig();
            LogUtils.LOGGER.info("[FarmHelper] Config loaded.");
            return config;
        } catch (IOException e) {
            LogUtils.LOGGER.error("[FarmHelper] Failed to load config: {}", e.getMessage());
            return new FarmHelperConfig();
        }
    }

    public void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (Writer writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            LogUtils.LOGGER.error("[FarmHelper] Failed to save config: {}", e.getMessage());
        }
    }
}
