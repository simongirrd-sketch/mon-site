package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Schedules macros to start/stop at specific real-world times.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class Scheduler implements IFeature {

    private static Scheduler instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    private LocalTime startTime = null;
    private LocalTime stopTime = null;
    private boolean scheduledMacroRunning = false;

    private final List<ScheduleEntry> entries = new ArrayList<>();

    public static Scheduler getInstance() {
        if (instance == null) instance = new Scheduler();
        return instance;
    }

    public void setSchedule(LocalTime start, LocalTime stop) {
        this.startTime = start;
        this.stopTime = stop;
    }

    public void addEntry(ScheduleEntry entry) {
        entries.add(entry);
    }

    @Override
    public String getName() { return "Scheduler"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        running = true;
        LogUtils.sendMessage("§a[Scheduler] §fEnabled.");
        if (startTime != null) {
            LogUtils.sendMessage("§a[Scheduler] §fWill start macro at §e" + startTime);
        }
        if (stopTime != null) {
            LogUtils.sendMessage("§a[Scheduler] §fWill stop macro at §e" + stopTime);
        }
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[Scheduler] §fDisabled.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        LocalTime now = LocalTime.now();

        if (startTime != null && !scheduledMacroRunning) {
            if (isTimeNow(now, startTime)) {
                scheduledMacroRunning = true;
                LogUtils.sendMessage("§a[Scheduler] §fStart time reached. Starting macro...");
                // MacroHandler.getInstance().startMacro(currentMacro);
            }
        }

        if (stopTime != null && scheduledMacroRunning) {
            if (isTimeNow(now, stopTime)) {
                scheduledMacroRunning = false;
                MacroHandler.getInstance().stopMacro();
                LogUtils.sendMessage("§a[Scheduler] §fStop time reached. Stopping macro.");
            }
        }

        // Process schedule entries
        for (ScheduleEntry entry : entries) {
            if (!entry.executed && isTimeNow(now, entry.time)) {
                entry.run();
                entry.executed = true;
            }
        }
    }

    private boolean isTimeNow(LocalTime now, LocalTime target) {
        return now.getHour() == target.getHour() &&
                now.getMinute() == target.getMinute() &&
                now.getSecond() == target.getSecond();
    }

    public static class ScheduleEntry {
        public final LocalTime time;
        public final Runnable action;
        public boolean executed = false;

        public ScheduleEntry(LocalTime time, Runnable action) {
            this.time = time;
            this.action = action;
        }

        public void run() { action.run(); }
    }

    @Override
    public void onWorldLoad() { scheduledMacroRunning = false; }
}
