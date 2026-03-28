package com.jelly.farmhelperv2.feature;

import com.jelly.farmhelperv2.feature.impl.*;
import com.jelly.farmhelperv2.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all FarmHelper features.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class FeatureManager {

    private static FeatureManager instance;
    private final List<IFeature> features = new ArrayList<>();

    public static FeatureManager getInstance() {
        if (instance == null) instance = new FeatureManager();
        return instance;
    }

    public void registerAllFeatures() {
        // Automation features
        register(AutoBazaar.getInstance());
        register(AutoComposter.getInstance());
        register(AutoCookie.getInstance());
        register(AutoGodPot.getInstance());
        register(AutoPestExchange.getInstance());
        register(AutoReconnect.getInstance());
        register(AutoRepellent.getInstance());
        register(AutoSell.getInstance());
        register(AutoSprayonator.getInstance());
        register(AutoWardrobe.getInstance());

        // Farming features
        register(PestFarmer.getInstance());
        register(PestsDestroyer.getInstance());
        register(PlotCleaningHelper.getInstance());
        register(ProfitCalculator.getInstance());

        // Monitoring
        register(BPSTracker.getInstance());
        register(LagDetector.getInstance());
        register(DesyncChecker.getInstance());
        register(LeaveTimer.getInstance());

        // Player assistance
        register(AntiStuck.getInstance());
        register(Freelook.getInstance());
        register(PerformanceMode.getInstance());
        register(PetSwapper.getInstance());
        register(RancherSpeedSetter.getInstance());
        register(UngrabMouse.getInstance());
        register(VisitorsMacro.getInstance());
        register(Scheduler.getInstance());

        LogUtils.sendMessage("§a[FarmHelper] §fRegistered " + features.size() + " features.");
    }

    private void register(IFeature feature) {
        features.add(feature);
    }

    public void onTick() {
        for (IFeature feature : features) {
            if (feature.isRunning() && !feature.shouldPause()) {
                try {
                    feature.onTick();
                } catch (Exception e) {
                    LogUtils.sendError("[FarmHelper] Error in feature " + feature.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    public void onWorldLoad() {
        for (IFeature feature : features) {
            feature.onWorldLoad();
        }
    }

    public void stopAll() {
        for (IFeature feature : features) {
            if (feature.isRunning()) feature.stop();
            feature.onWorldUnload();
        }
    }

    public List<IFeature> getFeatures() {
        return features;
    }

    public <T extends IFeature> T getFeature(Class<T> clazz) {
        for (IFeature f : features) {
            if (clazz.isInstance(f)) return clazz.cast(f);
        }
        return null;
    }
}
