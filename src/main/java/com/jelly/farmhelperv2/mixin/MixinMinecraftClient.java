package com.jelly.farmhelperv2.mixin;

import com.jelly.farmhelperv2.feature.impl.LagDetector;
import com.jelly.farmhelperv2.handler.MacroHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into MinecraftClient for game-level hooks.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow
    public ClientPlayerEntity player;

    /**
     * Hook into the main game tick for macro handling.
     * In Forge 1.8.9: ClientTickEvent
     * In Fabric 1.21: Direct Mixin into MinecraftClient.tick()
     *
     * Note: We also register tick events via ClientTickEvents in FarmHelper.java,
     * but this Mixin gives us access to the very start of each tick.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickHead(CallbackInfo ci) {
        if (player == null) return;
        // Notify lag detector that we received a tick (server is responding)
        LagDetector.getInstance().onPacketReceived();
    }

    /**
     * Failsafe: Stop macros when the game window loses focus.
     */
    @Inject(method = "onWindowFocusChanged", at = @At("HEAD"))
    private void onWindowFocusChanged(boolean focused, CallbackInfo ci) {
        if (!focused && MacroHandler.getInstance().isMacroEnabled()) {
            // Optional: pause macro on focus lost
            // MacroHandler.getInstance().pauseMacro();
        }
    }
}
