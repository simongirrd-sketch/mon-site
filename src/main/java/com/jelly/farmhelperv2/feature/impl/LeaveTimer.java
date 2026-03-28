package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

/**
 * Automatically leaves the server after a configured duration.
 * Useful for scheduled farming sessions.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class LeaveTimer implements IFeature {

    private static LeaveTimer instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    private long durationMs = 0;
    private long startTime = 0;

    public static LeaveTimer getInstance() {
        if (instance == null) instance = new LeaveTimer();
        return instance;
    }

    public void setDurationMinutes(int minutes) {
        this.durationMs = (long) minutes * 60 * 1000;
    }

    @Override
    public String getName() { return "LeaveTimer"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (durationMs <= 0) {
            LogUtils.sendWarning("[LeaveTimer] Duration not set!");
            return;
        }
        running = true;
        startTime = System.currentTimeMillis();
        long minutes = durationMs / 60000;
        LogUtils.sendMessage("§a[LeaveTimer] §fWill disconnect in §e" + minutes + " §fminutes.");
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= durationMs) {
            LogUtils.sendMessage("§a[LeaveTimer] §fTimer expired. Disconnecting...");
            MacroHandler.getInstance().stopAll();

            // Disconnect from server in Fabric 1.21
            ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
            if (networkHandler != null) {
                networkHandler.getConnection().disconnect(
                        net.minecraft.text.Text.literal("[FarmHelper] LeaveTimer expired.")
                );
            }
            stop();
        }
    }

    public long getRemainingMs() {
        if (!running) return 0;
        return Math.max(0, durationMs - (System.currentTimeMillis() - startTime));
    }

    @Override
    public void onWorldLoad() { stop(); }
}
