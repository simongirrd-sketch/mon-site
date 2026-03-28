package com.jelly.farmhelperv2.mixin;

import com.jelly.farmhelperv2.feature.impl.Freelook;
import com.jelly.farmhelperv2.feature.impl.UngrabMouse;
import net.minecraft.client.Keyboard;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into Mouse (not Keyboard, despite the class name) for Freelook and UngrabMouse.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * In Forge 1.8.9: FOVEvent, MouseEvent
 * In Fabric 1.21: Mixin into Mouse.updateMouse() to intercept mouse movement
 */
@Mixin(Mouse.class)
public class MixinKeyboard {

    /**
     * Intercept mouse movement for Freelook.
     * When Freelook is active, redirect mouse movement to the camera instead of the player.
     */
    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    private void onMouseMove(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        Freelook freelook = Freelook.getInstance();
        if (freelook.isRunning()) {
            // Apply mouse movement to freelook camera instead of player rotation
            freelook.rotateCameraBy(cursorDeltaX, cursorDeltaY);
            ci.cancel(); // Cancel normal mouse rotation
        }

        UngrabMouse ungrab = UngrabMouse.getInstance();
        if (ungrab.isRunning()) {
            ci.cancel(); // Cancel all mouse input to the game
        }
    }
}
