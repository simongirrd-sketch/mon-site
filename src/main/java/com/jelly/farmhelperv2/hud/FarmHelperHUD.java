package com.jelly.farmhelperv2.hud;

import com.jelly.farmhelperv2.FarmHelper;
import com.jelly.farmhelperv2.config.FarmHelperConfig;
import com.jelly.farmhelperv2.feature.impl.BPSTracker;
import com.jelly.farmhelperv2.feature.impl.ProfitCalculator;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the FarmHelper HUD overlay.
 * Converted from Forge 1.8.9 RenderGameOverlayEvent to Fabric 1.21.1 HudRenderCallback.
 *
 * Register with: HudRenderCallback.EVENT.register(FarmHelperHUD::render)
 */
public class FarmHelperHUD {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void register() {
        HudRenderCallback.EVENT.register(FarmHelperHUD::render);
    }

    /**
     * Main HUD render method.
     * Called each frame during HUD rendering.
     *
     * @param context The draw context (replaces MatrixStack in 1.20+)
     * @param tickDelta Partial tick delta
     */
    public static void render(DrawContext context, float tickDelta) {
        FarmHelperConfig config = FarmHelper.config;
        if (!config.showHUD) return;
        if (mc.player == null || mc.world == null) return;
        if (mc.options.hudHidden) return;

        int x = config.hudX;
        int y = config.hudY;
        int lineHeight = 12;
        int line = 0;

        List<String> lines = buildHUDLines(config);

        // Background box
        if (!lines.isEmpty()) {
            int boxWidth = lines.stream().mapToInt(l -> mc.textRenderer.getWidth(l)).max().orElse(0) + 6;
            int boxHeight = lines.size() * lineHeight + 4;
            context.fill(x - 2, y - 2, x + boxWidth, y + boxHeight, 0x88000000);
        }

        // Render lines
        for (String text : lines) {
            context.drawTextWithShadow(mc.textRenderer, text, x, y + line * lineHeight, 0xFFFFFFFF);
            line++;
        }
    }

    private static List<String> buildHUDLines(FarmHelperConfig config) {
        List<String> lines = new ArrayList<>();
        MacroHandler macro = MacroHandler.getInstance();

        // Macro status
        if (config.showMacroStatus) {
            if (macro.isMacroEnabled()) {
                String status = macro.isPaused() ? "§ePaused" : "§aRunning";
                String macroName = macro.getCurrentMacro() != null ?
                        macro.getCurrentMacro().getName() : "Unknown";
                lines.add("§f[FarmHelper] " + status + " §7- §f" + macroName);
            } else {
                lines.add("§f[FarmHelper] §cStopped");
            }
        }

        // Runtime
        if (config.showRuntime && macro.isMacroEnabled()) {
            long ms = macro.getMacroRunTime();
            long hours = ms / 3600000;
            long minutes = (ms % 3600000) / 60000;
            long seconds = (ms % 60000) / 1000;
            lines.add(String.format("§7Time: §f%02d:%02d:%02d", hours, minutes, seconds));
        }

        // BPS
        if (config.showBPS) {
            int bps = MacroHandler.getInstance().getBlocksPerSecond();
            lines.add("§7BPS: §e" + bps);
        }

        // Profit
        if (config.showProfitPerHour) {
            double profit = MacroHandler.getInstance().getProfitPerHour();
            lines.add(String.format("§7Profit/h: §6%.0f coins", profit));
        }

        return lines;
    }
}
