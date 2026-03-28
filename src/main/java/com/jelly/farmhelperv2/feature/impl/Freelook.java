package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;

/**
 * Allows looking around freely without rotating the player character.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * In Fabric 1.21, this is achieved by storing the original camera angles
 * and restoring them each tick via a Mixin into GameRenderer.
 */
public class Freelook implements IFeature {

    private static Freelook instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;

    private float savedYaw = 0;
    private float savedPitch = 0;
    private float cameraYaw = 0;
    private float cameraPitch = 0;

    public static Freelook getInstance() {
        if (instance == null) instance = new Freelook();
        return instance;
    }

    @Override
    public String getName() { return "Freelook"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (mc.player == null) return;
        savedYaw = mc.player.getYaw();
        savedPitch = mc.player.getPitch();
        cameraYaw = savedYaw;
        cameraPitch = savedPitch;
        running = true;
        LogUtils.sendMessage("§a[Freelook] §fEnabled. Camera is now free.");
    }

    @Override
    public void stop() {
        // Restore the player's actual yaw/pitch when disabling
        if (mc.player != null) {
            mc.player.setYaw(savedYaw);
            mc.player.setPitch(savedPitch);
        }
        running = false;
        LogUtils.sendMessage("§a[Freelook] §fDisabled.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        // Save current player rotation (this is what the server sees)
        savedYaw = mc.player.getYaw();
        savedPitch = mc.player.getPitch();
        // The camera rotation (cameraYaw/cameraPitch) is handled in MixinGameRenderer
    }

    /**
     * Called by MixinGameRenderer to get the camera rotation for rendering.
     * Returns camera yaw while keeping player rotation unchanged.
     */
    public float getCameraYaw() { return cameraYaw; }
    public float getCameraPitch() { return cameraPitch; }

    /**
     * Called by MixinKeyboard when mouse is moved during freelook.
     */
    public void rotateCameraBy(double deltaX, double deltaY) {
        cameraYaw += (float) deltaX * 0.15f;
        cameraPitch = Math.max(-90, Math.min(90, cameraPitch + (float) deltaY * 0.15f));
    }
}
