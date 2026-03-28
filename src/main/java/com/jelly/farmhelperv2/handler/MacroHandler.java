package com.jelly.farmhelperv2.handler;

import com.jelly.farmhelperv2.config.FarmHelperConfig;
import com.jelly.farmhelperv2.macro.AbstractMacro;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages macro execution and state.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class MacroHandler {

    private static MacroHandler instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private final List<AbstractMacro> registeredMacros = new ArrayList<>();
    private AbstractMacro currentMacro = null;
    private boolean macroEnabled = false;
    private long macroStartTime = 0;
    private long pauseStartTime = 0;
    private boolean paused = false;

    // Stats
    private int blocksPerSecond = 0;
    private long totalCropsCollected = 0;
    private double profitPerHour = 0;

    public static MacroHandler getInstance() {
        if (instance == null) instance = new MacroHandler();
        return instance;
    }

    public void onTick() {
        if (mc.player == null) return;
        if (!macroEnabled || currentMacro == null) return;
        if (paused) return;

        try {
            currentMacro.onTick();
        } catch (Exception e) {
            LogUtils.sendError("[MacroHandler] Macro error: " + e.getMessage());
            stopMacro();
        }
    }

    public void startMacro(AbstractMacro macro) {
        if (currentMacro != null && currentMacro.isRunning()) {
            currentMacro.stop();
        }
        currentMacro = macro;
        macroEnabled = true;
        macroStartTime = System.currentTimeMillis();
        paused = false;
        macro.start();
        LogUtils.sendMessage("§a[FarmHelper] §fMacro started: §e" + macro.getName());
    }

    public void stopMacro() {
        if (currentMacro != null) {
            currentMacro.stop();
            LogUtils.sendMessage("§a[FarmHelper] §fMacro stopped: §e" + currentMacro.getName());
            currentMacro = null;
        }
        macroEnabled = false;
        paused = false;
    }

    public void stopAll() {
        stopMacro();
        for (AbstractMacro macro : registeredMacros) {
            if (macro.isRunning()) macro.stop();
        }
    }

    public void pauseMacro() {
        if (macroEnabled && !paused) {
            paused = true;
            pauseStartTime = System.currentTimeMillis();
            if (currentMacro != null) currentMacro.onPause();
            LogUtils.sendDebug("[MacroHandler] Macro paused.");
        }
    }

    public void resumeMacro() {
        if (macroEnabled && paused) {
            paused = false;
            if (currentMacro != null) currentMacro.onResume();
            LogUtils.sendDebug("[MacroHandler] Macro resumed.");
        }
    }

    public void onWorldLoad() {
        stopAll();
        blocksPerSecond = 0;
        totalCropsCollected = 0;
    }

    // Getters / setters
    public boolean isMacroEnabled() { return macroEnabled; }
    public boolean isPaused() { return paused; }
    public AbstractMacro getCurrentMacro() { return currentMacro; }
    public long getMacroStartTime() { return macroStartTime; }
    public long getMacroRunTime() {
        if (!macroEnabled) return 0;
        return System.currentTimeMillis() - macroStartTime;
    }
    public int getBlocksPerSecond() { return blocksPerSecond; }
    public void setBlocksPerSecond(int bps) { this.blocksPerSecond = bps; }
    public long getTotalCropsCollected() { return totalCropsCollected; }
    public void addCropsCollected(long amount) { this.totalCropsCollected += amount; }
    public double getProfitPerHour() { return profitPerHour; }
    public void setProfitPerHour(double profit) { this.profitPerHour = profit; }
}
