package com.jelly.farmhelperv2.macro;

import com.jelly.farmhelperv2.util.KeyBindUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Base class for all FarmHelper macros.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public abstract class AbstractMacro {

    protected final MinecraftClient mc = MinecraftClient.getInstance();
    protected boolean running = false;
    protected int tickCounter = 0;

    public abstract String getName();

    public abstract void onTick();

    public void start() {
        running = true;
        tickCounter = 0;
        LogUtils.sendDebug("[" + getName() + "] Started.");
    }

    public void stop() {
        running = false;
        KeyBindUtils.releaseAllMovementKeys();
        LogUtils.sendDebug("[" + getName() + "] Stopped.");
    }

    public void onPause() {
        KeyBindUtils.releaseAllMovementKeys();
    }

    public void onResume() {
        LogUtils.sendDebug("[" + getName() + "] Resumed.");
    }

    public boolean isRunning() {
        return running;
    }

    protected void incrementTick() {
        tickCounter++;
    }

    protected boolean waitTicks(int ticks) {
        return tickCounter >= ticks;
    }
}
