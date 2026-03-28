package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.APIUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculates farming profit per hour using Bazaar API prices.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class ProfitCalculator implements IFeature {

    private static ProfitCalculator instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    private final Map<String, Double> itemPrices = new HashMap<>();
    private long cropsAtStart = 0;
    private long startTime = 0;
    private double profitPerHour = 0;

    private int updateInterval = 600; // Update every 30 seconds (600 ticks)
    private int tickCounter = 0;

    public static ProfitCalculator getInstance() {
        if (instance == null) instance = new ProfitCalculator();
        return instance;
    }

    @Override
    public String getName() { return "ProfitCalculator"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        running = true;
        cropsAtStart = MacroHandler.getInstance().getTotalCropsCollected();
        startTime = System.currentTimeMillis();
        tickCounter = 0;
        fetchPrices();
        LogUtils.sendMessage("§a[ProfitCalculator] §fStarted tracking profit.");
    }

    @Override
    public void stop() {
        running = false;
        LogUtils.sendMessage("§a[ProfitCalculator] §fProfit/hour: §e" +
                String.format("%.0f", profitPerHour) + " coins");
    }

    @Override
    public void onTick() {
        tickCounter++;
        if (tickCounter % updateInterval == 0) {
            updateProfit();
            fetchPrices();
        }
    }

    private void fetchPrices() {
        APIUtils.fetchBazaarPrices(json -> {
            if (json == null) return;
            // Parse prices for common crop items
            String[] crops = {"WHEAT", "CARROT_ITEM", "POTATO_ITEM", "NETHER_WART",
                    "SUGAR_CANE", "CACTUS", "MELON", "PUMPKIN", "COCOA_BEANS"};
            for (String crop : crops) {
                try {
                    double price = json.getAsJsonObject("products")
                            .getAsJsonObject(crop)
                            .getAsJsonObject("quick_status")
                            .get("sellPrice").getAsDouble();
                    itemPrices.put(crop, price);
                } catch (Exception ignored) {}
            }
        });
    }

    private void updateProfit() {
        long totalCrops = MacroHandler.getInstance().getTotalCropsCollected();
        long cropsCollected = totalCrops - cropsAtStart;
        double elapsedHours = (System.currentTimeMillis() - startTime) / 3600000.0;
        if (elapsedHours > 0) {
            // Simplified: assume average crop price
            double avgPrice = itemPrices.values().stream().mapToDouble(d -> d).average().orElse(1.0);
            profitPerHour = (cropsCollected * avgPrice) / elapsedHours;
            MacroHandler.getInstance().setProfitPerHour(profitPerHour);
        }
    }

    public double getProfitPerHour() { return profitPerHour; }
    public Map<String, Double> getItemPrices() { return itemPrices; }

    @Override
    public void onWorldLoad() { if (running) { cropsAtStart = 0; startTime = System.currentTimeMillis(); } }
}
