package com.jelly.farmhelperv2.mixin;

import com.jelly.farmhelperv2.feature.impl.Freelook;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into GameRenderer for Freelook camera control.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * In Forge 1.8.9: Used RenderWorldLastEvent + GL11 camera manipulation
 * In Fabric 1.21: Use Mixin into GameRenderer.renderWorld()
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    /**
     * Override camera rotation when Freelook is active.
     * The player's actual yaw/pitch remain unchanged (server doesn't know we're looking elsewhere).
     */
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        // Freelook camera override is handled during the camera setup phase
        // See MixinCamera for the actual implementation
    }
}
