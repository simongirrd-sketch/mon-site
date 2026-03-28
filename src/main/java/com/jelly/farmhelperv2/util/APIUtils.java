package com.jelly.farmhelperv2.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Hypixel/Bazaar API utilities.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Uses Java's built-in HTTP client (java.net) instead of Apache HttpClient.
 * All network calls are done asynchronously to avoid blocking the game thread.
 */
public class APIUtils {

    private static final String BAZAAR_URL = "https://api.hypixel.net/skyblock/bazaar";
    private static String apiKey = "";

    public static void setApiKey(String key) {
        apiKey = key;
    }

    /**
     * Fetch Bazaar prices asynchronously.
     * Callback is invoked on completion with the JSON result (or null on error).
     */
    public static void fetchBazaarPrices(Consumer<JsonObject> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return fetchJson(BAZAAR_URL + "?key=" + apiKey);
            } catch (IOException e) {
                LogUtils.sendError("[APIUtils] Failed to fetch bazaar: " + e.getMessage());
                return null;
            }
        }).thenAccept(json -> {
            if (json != null) {
                callback.accept(json);
            }
        });
    }

    /**
     * Get the sell price of an item from the Bazaar.
     */
    public static CompletableFuture<Double> getBazaarSellPrice(String itemId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject data = fetchJson(BAZAAR_URL + "?key=" + apiKey);
                if (data == null) return 0.0;
                JsonObject products = data.getAsJsonObject("products");
                if (!products.has(itemId)) return 0.0;
                JsonObject product = products.getAsJsonObject(itemId);
                JsonObject sellSummary = product.getAsJsonObject("quick_status");
                return sellSummary.get("sellPrice").getAsDouble();
            } catch (Exception e) {
                LogUtils.sendError("[APIUtils] Error fetching sell price: " + e.getMessage());
                return 0.0;
            }
        });
    }

    /**
     * Fetch JSON from a URL.
     */
    public static JsonObject fetchJson(String urlString) throws IOException {
        URL url = URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "FarmHelperV2/2.10.0");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP " + responseCode);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return JsonParser.parseString(sb.toString()).getAsJsonObject();
    }
}
