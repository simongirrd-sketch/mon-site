package com.jelly.farmhelperv2.config;

import com.jelly.farmhelperv2.FarmHelper;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * In-game configuration screen using Cloth Config.
 * Replaces the Forge configuration GUI system.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class FarmHelperConfigScreen {

    /**
     * Build and return the config screen.
     *
     * @param parent The parent screen to return to when closed.
     */
    public static Screen build(Screen parent) {
        FarmHelperConfig config = FarmHelper.config;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("FarmHelper V2 - Configuration"))
                .setSavingRunnable(config::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // =====================
        // General Category
        // =====================
        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Debug Mode"), config.debugMode)
                .setDefaultValue(false)
                .setTooltip(Text.literal("Show debug messages in chat"))
                .setSaveConsumer(val -> config.debugMode = val)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Show HUD"), config.showHUD)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showHUD = val)
                .build());

        general.addEntry(entryBuilder
                .startStrField(Text.literal("Hypixel API Key"), config.apiKey)
                .setDefaultValue("")
                .setTooltip(Text.literal("Your Hypixel API key for Bazaar prices (/api new)"))
                .setSaveConsumer(val -> config.apiKey = val)
                .build());

        // =====================
        // Macro Category
        // =====================
        ConfigCategory macro = builder.getOrCreateCategory(Text.literal("Macro"));

        macro.addEntry(entryBuilder
                .startIntSlider(Text.literal("Farming Speed"), config.farmingSpeed, 100, 500)
                .setDefaultValue(400)
                .setTooltip(Text.literal("Rancher's Boots speed for farming"))
                .setSaveConsumer(val -> config.farmingSpeed = val)
                .build());

        macro.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Anti Stuck"), config.enableAntiStuck)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.enableAntiStuck = val)
                .build());

        macro.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Lag Detector"), config.enableLagDetector)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.enableLagDetector = val)
                .build());

        macro.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Desync Checker"), config.enableDesyncCheck)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.enableDesyncCheck = val)
                .build());

        // =====================
        // Features Category
        // =====================
        ConfigCategory features = builder.getOrCreateCategory(Text.literal("Features"));

        features.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Auto Sell"), config.autoSellEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.autoSellEnabled = val)
                .build());

        features.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Auto Bazaar"), config.autoBazaarEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.autoBazaarEnabled = val)
                .build());

        features.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Auto Composter"), config.autoComposterEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.autoComposterEnabled = val)
                .build());

        features.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Auto God Pot"), config.autoGodPotEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.autoGodPotEnabled = val)
                .build());

        features.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Auto Cookie"), config.autoCookieEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.autoCookieEnabled = val)
                .build());

        features.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Pests Destroyer"), config.pestsDestroyerEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.pestsDestroyerEnabled = val)
                .build());

        features.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Visitors Macro"), config.visitorsMacroEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.visitorsMacroEnabled = val)
                .build());

        features.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Performance Mode"), config.performanceModeEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.performanceModeEnabled = val)
                .build());

        features.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Ungrab Mouse"), config.ungrabMouseEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.ungrabMouseEnabled = val)
                .build());

        features.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Freelook"), config.freelookEnabled)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.freelookEnabled = val)
                .build());

        // =====================
        // HUD Category
        // =====================
        ConfigCategory hud = builder.getOrCreateCategory(Text.literal("HUD"));

        hud.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Show BPS"), config.showBPS)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showBPS = val)
                .build());

        hud.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Show Profit/Hour"), config.showProfitPerHour)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showProfitPerHour = val)
                .build());

        hud.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Show Macro Status"), config.showMacroStatus)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.showMacroStatus = val)
                .build());

        hud.addEntry(entryBuilder
                .startIntField(Text.literal("HUD X Position"), config.hudX)
                .setDefaultValue(10)
                .setSaveConsumer(val -> config.hudX = val)
                .build());

        hud.addEntry(entryBuilder
                .startIntField(Text.literal("HUD Y Position"), config.hudY)
                .setDefaultValue(10)
                .setSaveConsumer(val -> config.hudY = val)
                .build());

        // =====================
        // Failsafe Category
        // =====================
        ConfigCategory failsafe = builder.getOrCreateCategory(Text.literal("Failsafe"));

        failsafe.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Stop on Player Nearby"), config.stopOnPlayerNearby)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Stop macro when another player comes near"))
                .setSaveConsumer(val -> config.stopOnPlayerNearby = val)
                .build());

        failsafe.addEntry(entryBuilder
                .startDoubleField(Text.literal("Player Nearby Radius"), config.playerNearbyRadius)
                .setDefaultValue(20.0)
                .setSaveConsumer(val -> config.playerNearbyRadius = val)
                .build());

        failsafe.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Stop on Chat Message"), config.stopOnChatMessage)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.stopOnChatMessage = val)
                .build());

        failsafe.addEntry(entryBuilder
                .startStrField(Text.literal("Stop Chat Keyword"), config.stopChatKeyword)
                .setDefaultValue("")
                .setTooltip(Text.literal("Stop macro if this keyword appears in chat"))
                .setSaveConsumer(val -> config.stopChatKeyword = val)
                .build());

        return builder.build();
    }
}
