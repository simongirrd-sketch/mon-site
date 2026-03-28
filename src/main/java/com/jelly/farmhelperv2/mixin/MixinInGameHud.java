package com.jelly.farmhelperv2.mixin;

import com.jelly.farmhelperv2.feature.impl.BPSTracker;
import com.jelly.farmhelperv2.failsafe.ChatFailsafe;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into InGameHud for chat monitoring and action bar reading.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * In Forge 1.8.9: ClientChatReceivedEvent, RenderGameOverlayEvent
 * In Fabric 1.21: Mixin into InGameHud.setOverlayMessage() and addChatMessage()
 */
@Mixin(InGameHud.class)
public class MixinInGameHud {

    /**
     * Intercept action bar messages (overlay messages like "✦ Active Effects ✦").
     * Used to read Skyblock status from the action bar.
     */
    @Inject(method = "setOverlayMessage", at = @At("HEAD"))
    private void onActionBar(Text message, boolean tinted, CallbackInfo ci) {
        if (message == null) return;
        String text = message.getString();
        // Pass to failsafe system for monitoring
        ChatFailsafe.getInstance().onActionBar(text);
    }

    /**
     * Intercept chat messages.
     * Used for failsafe triggers and state detection.
     */
    @Inject(method = "addChatMessage", at = @At("HEAD"))
    private void onChatMessage(net.minecraft.client.gui.hud.MessageIndicator indicator,
                               net.minecraft.network.message.MessageSignatureData sig,
                               net.minecraft.network.message.SignedMessage message,
                               boolean onlyShowIfSystemEnabled,
                               CallbackInfo ci) {
        if (message == null) return;
        String text = message.getContent().getString();
        ChatFailsafe.getInstance().onChatMessage(text);
    }
}
