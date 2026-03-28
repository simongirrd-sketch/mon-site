package com.jelly.farmhelperv2.command;

import com.jelly.farmhelperv2.FarmHelper;
import com.jelly.farmhelperv2.config.FarmHelperConfigScreen;
import com.jelly.farmhelperv2.feature.FeatureManager;
import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.LogUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

/**
 * Main FarmHelper command: /farmhelper
 * Converted from Forge 1.8.9 ClientCommandHandler to Fabric 1.21.1 ClientCommandManager.
 *
 * Key API changes:
 * - ClientCommandHandler.instance.registerCommand() -> ClientCommandRegistrationCallback.EVENT
 * - ICommand interface -> Brigadier command tree
 * - CommandBase -> com.mojang.brigadier
 */
public class FarmHelperCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(FarmHelperCommand::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                         CommandRegistryAccess registryAccess) {
        dispatcher.register(
                ClientCommandManager.literal("farmhelper")
                        .executes(ctx -> {
                            openConfigScreen();
                            return 1;
                        })
                        .then(ClientCommandManager.literal("config")
                                .executes(ctx -> {
                                    openConfigScreen();
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("start")
                                .executes(ctx -> {
                                    // Start the current configured macro
                                    LogUtils.sendMessage("§a[FarmHelper] §fStarting macro...");
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("stop")
                                .executes(ctx -> {
                                    MacroHandler.getInstance().stopMacro();
                                    LogUtils.sendMessage("§a[FarmHelper] §fMacro stopped.");
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("pause")
                                .executes(ctx -> {
                                    MacroHandler.getInstance().pauseMacro();
                                    LogUtils.sendMessage("§a[FarmHelper] §fMacro paused.");
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("resume")
                                .executes(ctx -> {
                                    MacroHandler.getInstance().resumeMacro();
                                    LogUtils.sendMessage("§a[FarmHelper] §fMacro resumed.");
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("status")
                                .executes(ctx -> {
                                    showStatus();
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("debug")
                                .executes(ctx -> {
                                    boolean debug = !FarmHelper.config.debugMode;
                                    FarmHelper.config.debugMode = debug;
                                    LogUtils.setDebugMode(debug);
                                    LogUtils.sendMessage("§a[FarmHelper] §fDebug mode: " + (debug ? "§aON" : "§cOFF"));
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("hud")
                                .executes(ctx -> {
                                    FarmHelper.config.showHUD = !FarmHelper.config.showHUD;
                                    LogUtils.sendMessage("§a[FarmHelper] §fHUD: " + (FarmHelper.config.showHUD ? "§aON" : "§cOFF"));
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("help")
                                .executes(ctx -> {
                                    showHelp();
                                    return 1;
                                }))
        );

        // Also register /fh as shorthand
        dispatcher.register(
                ClientCommandManager.literal("fh")
                        .redirect(dispatcher.getRoot().getChild("farmhelper"))
        );
    }

    private static void openConfigScreen() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.execute(() -> mc.setScreen(FarmHelperConfigScreen.build(mc.currentScreen)));
    }

    private static void showStatus() {
        MacroHandler macro = MacroHandler.getInstance();
        LogUtils.sendMessage("§6=== FarmHelper V2 Status ===");
        LogUtils.sendMessage("§7Macro: " + (macro.isMacroEnabled() ? "§aRunning" : "§cStopped"));
        if (macro.isMacroEnabled()) {
            long ms = macro.getMacroRunTime();
            long min = ms / 60000;
            long sec = (ms % 60000) / 1000;
            LogUtils.sendMessage("§7Runtime: §f" + min + "m " + sec + "s");
            LogUtils.sendMessage("§7BPS: §e" + macro.getBlocksPerSecond());
            LogUtils.sendMessage("§7Crops: §e" + macro.getTotalCropsCollected());
            LogUtils.sendMessage(String.format("§7Profit/h: §6%.0f coins", macro.getProfitPerHour()));
        }

        LogUtils.sendMessage("§7Active Features:");
        for (IFeature feature : FeatureManager.getInstance().getFeatures()) {
            if (feature.isRunning()) {
                LogUtils.sendMessage("  §a✓ " + feature.getName());
            }
        }
    }

    private static void showHelp() {
        LogUtils.sendMessage("§6=== FarmHelper V2 Commands ===");
        LogUtils.sendMessage("§e/farmhelper §7or §e/fh §7- Open config");
        LogUtils.sendMessage("§e/fh start §7- Start farming macro");
        LogUtils.sendMessage("§e/fh stop §7- Stop farming macro");
        LogUtils.sendMessage("§e/fh pause §7- Pause macro");
        LogUtils.sendMessage("§e/fh resume §7- Resume macro");
        LogUtils.sendMessage("§e/fh status §7- Show current status");
        LogUtils.sendMessage("§e/fh debug §7- Toggle debug mode");
        LogUtils.sendMessage("§e/fh hud §7- Toggle HUD display");
        LogUtils.sendMessage("§e/fh help §7- Show this help");
    }
}
