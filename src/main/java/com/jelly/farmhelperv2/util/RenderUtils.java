package com.jelly.farmhelperv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * Rendering utility methods.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 *
 * Key API changes:
 * - GL11 calls replaced by MatrixStack + VertexConsumer
 * - GlStateManager replaced by RenderSystem
 * - drawString -> textRenderer.draw(MatrixStack, ...)
 * - drawRect -> manual vertex drawing
 * - Event RenderGameOverlayEvent -> HudRenderCallback (Fabric API)
 */
public class RenderUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Draw a string on the HUD.
     * Must be called within a HUD render callback.
     */
    public static void drawString(MatrixStack matrices, String text, float x, float y, int color) {
        TextRenderer textRenderer = mc.textRenderer;
        textRenderer.draw(matrices, text, x, y, color);
    }

    /**
     * Draw a string with shadow on the HUD.
     */
    public static void drawStringWithShadow(MatrixStack matrices, String text, float x, float y, int color) {
        TextRenderer textRenderer = mc.textRenderer;
        textRenderer.drawWithShadow(matrices, text, x, y, color);
    }

    /**
     * Draw a filled rectangle on the HUD.
     * Replaces Forge's drawRect/drawGradientRect.
     */
    public static void drawRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float a = (color >> 24 & 0xFF) / 255.0f;
        float r = (color >> 16 & 0xFF) / 255.0f;
        float g = (color >> 8 & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y + height, 0).color(r, g, b, a);
        buffer.vertex(matrix, x + width, y + height, 0).color(r, g, b, a);
        buffer.vertex(matrix, x + width, y, 0).color(r, g, b, a);
        buffer.vertex(matrix, x, y, 0).color(r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
    }

    /**
     * Draw an outline box around a block in the world.
     * Must be called during WorldRenderLastCallback.
     */
    public static void drawBlockOutline(MatrixStack matrices, BlockPos pos, float r, float g, float b, float a) {
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        Box box = new Box(
                pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z,
                pos.getX() + 1 - camPos.x, pos.getY() + 1 - camPos.y, pos.getZ() + 1 - camPos.z
        );
        drawBox(matrices, box, r, g, b, a);
    }

    /**
     * Draw a box outline in 3D world space.
     */
    public static void drawBox(MatrixStack matrices, Box box, float r, float g, float b, float a) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float x1 = (float) box.minX, y1 = (float) box.minY, z1 = (float) box.minZ;
        float x2 = (float) box.maxX, y2 = (float) box.maxY, z2 = (float) box.maxZ;

        // Bottom face
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        // Top face
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        // Vertical edges
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.disableBlend();
    }

    /**
     * Get the text width for centering purposes.
     */
    public static int getStringWidth(String text) {
        return mc.textRenderer.getWidth(text);
    }
}
