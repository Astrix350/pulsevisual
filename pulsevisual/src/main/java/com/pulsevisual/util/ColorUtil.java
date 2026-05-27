package com.pulsevisual.util;

import com.pulsevisual.config.PulseConfig;

public class ColorUtil {

    private static float chromaHue = 0f;

    /**
     * Returns ARGB int for current chroma hue.
     * Call tick() once per frame to animate.
     */
    public static int getChromaColor(float alpha) {
        float[] rgb = hsvToRgb(chromaHue, 1f, 1f);
        int a = (int)(alpha * 255) & 0xFF;
        int r = (int)(rgb[0] * 255) & 0xFF;
        int g = (int)(rgb[1] * 255) & 0xFF;
        int b = (int)(rgb[2] * 255) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Offset chroma hue by x (for horizontal chroma on text/bars) */
    public static int getChromaColor(float alpha, float offsetX) {
        float hue = (chromaHue + offsetX * 0.002f) % 1f;
        float[] rgb = hsvToRgb(hue, 1f, 1f);
        int a = (int)(alpha * 255) & 0xFF;
        int r = (int)(rgb[0] * 255) & 0xFF;
        int g = (int)(rgb[1] * 255) & 0xFF;
        int b = (int)(rgb[2] * 255) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static void tick(float deltaTime) {
        chromaHue = (chromaHue + deltaTime * PulseConfig.get().chromaSpeed * 0.5f) % 1f;
    }

    public static float[] hsvToRgb(float h, float s, float v) {
        float r = 0, g = 0, b = 0;
        int i = (int)(h * 6);
        float f = h * 6 - i;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);
        switch (i % 6) {
            case 0 -> { r = v; g = t; b = p; }
            case 1 -> { r = q; g = v; b = p; }
            case 2 -> { r = p; g = v; b = t; }
            case 3 -> { r = p; g = q; b = v; }
            case 4 -> { r = t; g = p; b = v; }
            case 5 -> { r = v; g = p; b = q; }
        }
        return new float[]{r, g, b};
    }

    /** Lerp between two ARGB colors */
    public static int lerp(int colorA, int colorB, float t) {
        int a1 = (colorA >> 24) & 0xFF, r1 = (colorA >> 16) & 0xFF,
            g1 = (colorA >> 8) & 0xFF,  b1 = colorA & 0xFF;
        int a2 = (colorB >> 24) & 0xFF, r2 = (colorB >> 16) & 0xFF,
            g2 = (colorB >> 8) & 0xFF,  b2 = colorB & 0xFF;
        int a = (int)(a1 + (a2 - a1) * t) & 0xFF;
        int r = (int)(r1 + (r2 - r1) * t) & 0xFF;
        int g = (int)(g1 + (g2 - g1) * t) & 0xFF;
        int b = (int)(b1 + (b2 - b1) * t) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int withAlpha(int color, float alpha) {
        return (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
    }

    /** Health color: green → yellow → red */
    public static int healthColor(float percent) {
        if (percent > 0.5f) return lerp(0xFF00FF00, 0xFFFFFF00, (1f - percent) * 2f);
        else                return lerp(0xFFFF0000, 0xFFFFFF00, percent * 2f);
    }
}
