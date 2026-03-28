package com.jelly.farmhelperv2.feature;

/**
 * Base interface for all FarmHelper features.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public interface IFeature {

    /**
     * @return The display name of this feature
     */
    String getName();

    /**
     * @return Whether this feature is currently enabled/running
     */
    boolean isRunning();

    /**
     * Called every client tick while in-game
     */
    void onTick();

    /**
     * Start/enable this feature
     */
    void start();

    /**
     * Stop/disable this feature
     */
    void stop();

    /**
     * Called when the player joins a world
     */
    default void onWorldLoad() {}

    /**
     * Called when the player leaves a world
     */
    default void onWorldUnload() {}

    /**
     * @return Whether this feature should be paused (e.g. in menu)
     */
    default boolean shouldPause() {
        return false;
    }
}
