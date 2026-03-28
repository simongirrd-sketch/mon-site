package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

/**
 * Automatically reconnects to the server after disconnect.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Key API changes:
 * - GuiDisconnected -> DisconnectedScreen
 * - Direct reconnect via ConnectScreen
 */
public class AutoReconnect implements IFeature {

    private static AutoReconnect instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int reconnectDelay = 0;
    private int reconnectAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RECONNECT_WAIT_TICKS = 200; // 10 seconds

    public static AutoReconnect getInstance() {
        if (instance == null) instance = new AutoReconnect();
        return instance;
    }

    @Override
    public String getName() { return "AutoReconnect"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        running = true;
        reconnectAttempts = 0;
        LogUtils.sendMessage("§a[AutoReconnect] §fEnabled.");
    }

    @Override
    public void stop() {
        running = false;
        reconnectDelay = 0;
        LogUtils.sendMessage("§a[AutoReconnect] §fDisabled.");
    }

    @Override
    public void onTick() {
        if (reconnectDelay > 0) {
            reconnectDelay--;
            return;
        }

        // Check if we're on the disconnect screen
        if (mc.currentScreen instanceof DisconnectedScreen) {
            if (reconnectAttempts >= MAX_ATTEMPTS) {
                LogUtils.sendMessage("§c[AutoReconnect] §fMax reconnect attempts reached.");
                stop();
                return;
            }
            reconnect();
        }
    }

    private void reconnect() {
        ServerInfo lastServer = mc.getCurrentServerEntry();
        if (lastServer == null) {
            LogUtils.sendError("[AutoReconnect] No server to reconnect to.");
            stop();
            return;
        }

        reconnectAttempts++;
        reconnectDelay = RECONNECT_WAIT_TICKS;
        LogUtils.sendMessage("§a[AutoReconnect] §fReconnecting... (attempt " + reconnectAttempts + "/" + MAX_ATTEMPTS + ")");

        // Use ConnectScreen to reconnect in Fabric 1.21
        net.minecraft.client.gui.screen.ConnectScreen.connect(
                new MultiplayerScreen(new TitleScreen()),
                mc,
                ServerAddress.parse(lastServer.address),
                lastServer,
                false,
                null
        );
    }

    @Override
    public void onWorldLoad() {
        reconnectAttempts = 0;
    }
}
