package com.jelly.farmhelperv2.mixin;

import com.jelly.farmhelperv2.handler.RotationHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into ClientPlayerEntity for rotation handling.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * In Forge 1.8.9: Used @SubscribeEvent on ClientTickEvent
 * In Fabric 1.21: Use @Inject into tick() method via Mixin
 */
@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayer {

    /**
     * Hook into the player's tick to apply smooth rotations.
     * Called every game tick for the local player.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        RotationHandler.getInstance().onTick();
    }
}
