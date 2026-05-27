package com.pulsevisual.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

public class RenderUtil {

    /**
     * Draw a filled rectangle with rounded corners.
     * radius should be <= min(width,height)/2
     */
    public static void drawRoundedRect(DrawContext ctx, float x, float y,
                                       float width, float height,
                                       float radius, int color) {
        // Center fill
        drawRect(ctx, x + radius, y, width - radius * 2, height, color);
        // Left/right strips
        drawRect(ctx, x, y + radius, radius, height - radius * 2, color);
        drawRect(ctx, x + width - radius, y + radius, radius, height - radius * 2, color);
        // Corners
        drawArc(ctx, x + radius,         y + radius,          radius, 180, 270, color);
        drawArc(ctx, x + width - radius, y + radius,          radius, 270, 360, color);
        drawArc(ctx, x + width - radius, y + height - radius, radius,   0,  90, color);
        drawArc(ctx, x + radius,         y + height - radius, radius,  90, 180, color);
    }

    /** Draw rounded rect outline only */
    public static void drawRoundedRectOutline(DrawContext ctx, float x, float y,
                                               float width, float height,
                                               float radius, float lineWidth, int color) {
        drawRect(ctx, x + radius, y,              width - radius * 2, lineWidth, color);
        drawRect(ctx, x + radius, y + height - lineWidth, width - radius * 2, lineWidth, color);
        drawRect(ctx, x,              y + radius, lineWidth, height - radius * 2, color);
        drawRect(ctx, x + width - lineWidth, y + radius, lineWidth, height - radius * 2, color);
    }

    public static void drawRect(DrawContext ctx, float x, float y, float w, float h, int color) {
        ctx.fill((int)x, (int)y, (int)(x + w), (int)(y + h), color);
    }

    /** Draw a gradient bar (left to right) */
    public static void drawGradientRect(DrawContext ctx, float x, float y,
                                        float w, float h, int colorLeft, int colorRight) {
        ctx.fillGradient((int)x, (int)y, (int)(x+w), (int)(y+h), colorLeft, colorRight);
    }

    /** Approximate arc using small rect segments */
    private static void drawArc(DrawContext ctx, float cx, float cy, float r,
                                 int startDeg, int endDeg, int color) {
        int steps = 8;
        for (int i = 0; i < steps; i++) {
            float a1 = (float)Math.toRadians(startDeg + (endDeg - startDeg) * (float)i / steps);
            float a2 = (float)Math.toRadians(startDeg + (endDeg - startDeg) * (float)(i+1) / steps);
            float x1 = cx + (float)Math.cos(a1) * r - r;
            float y1 = cy + (float)Math.sin(a1) * r - r;
            float x2 = cx + (float)Math.cos(a2) * r;
            float y2 = cy + (float)Math.sin(a2) * r;
            float minX = Math.min(x1, x2);
            float minY = Math.min(y1, y2);
            float maxX = Math.max(x1, x2) + 1;
            float maxY = Math.max(y1, y2) + 1;
            ctx.fill((int)minX, (int)minY, (int)maxX, (int)maxY, color);
        }
    }

    /** Draw a smooth progress bar with rounded ends */
    public static void drawProgressBar(DrawContext ctx, float x, float y, float w, float h,
                                        float progress, int bgColor, int fgColor) {
        float r = h / 2f;
        drawRoundedRect(ctx, x, y, w, h, r, bgColor);
        if (progress > 0f) {
            float filled = Math.max(h, w * progress);
            drawRoundedRect(ctx, x, y, filled, h, r, fgColor);
        }
    }

    /** Draw text with drop shadow */
    public static void drawText(DrawContext ctx, net.minecraft.client.font.TextRenderer font,
                                 String text, float x, float y, int color, boolean shadow) {
        ctx.drawText(font, text, (int)x, (int)y, color, shadow);
    }
}
