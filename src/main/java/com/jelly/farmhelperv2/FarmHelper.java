package com.jelly.farmhelperv2;

import com.jelly.farmhelperv2.command.FarmHelperCommand;
import com.jelly.farmhelperv2.config.FarmHelperConfig;
import com.jelly.farmhelperv2.failsafe.ChatFailsafe;
import com.jelly.farmhelperv2.feature.FeatureManager;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.hud.FarmHelperHUD;
import com.jelly.farmhelperv2.util.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

public class FarmHelper implements ClientModInitializer {

    public static final String MOD_ID = "farmhelperv2";
    public static final String MOD_NAME = "FarmHelper V2";
    public static final String VERSION = "2.10.0";

    public static FarmHelperConfig config;
    public static FarmHelper instance;

    private static MinecraftClient mc;

    @Override
    public void onInitializeClient() {
        instance = this;
        mc = MinecraftClient.getInstance();

        LogUtils.sendMessage("§a[FarmHelper] §fInitializing FarmHelper V2 " + VERSION + "...");

        // Load config
        config = FarmHelperConfig.load();

        // Register features
        FeatureManager.getInstance().registerAllFeatures();

        // Register HUD
        FarmHelperHUD.register();

        // Register commands
        FarmHelperCommand.register();

        // Client tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            GameStateHandler.getInstance().onTick();
            MacroHandler.getInstance().onTick();
            FeatureManager.getInstance().onTick();
            ChatFailsafe.getInstance().onTick();
        });

        // World join event
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            GameStateHandler.getInstance().onWorldLoad();
            MacroHandler.getInstance().onWorldLoad();
            FeatureManager.getInstance().onWorldLoad();
        });

        // World leave event
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            GameStateHandler.getInstance().onWorldUnload();
            MacroHandler.getInstance().stopAll();
            FeatureManager.getInstance().stopAll();
        });

        // Shutdown event
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            config.save();
            LogUtils.sendMessage("§a[FarmHelper] §fSaved config. Goodbye!");
        });

        LogUtils.sendMessage("§a[FarmHelper] §fInitialized successfully!");
    }

    public static MinecraftClient getMinecraftClient() {
        return mc;
    }
}
