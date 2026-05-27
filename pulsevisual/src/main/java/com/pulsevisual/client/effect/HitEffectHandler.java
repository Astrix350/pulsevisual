package com.pulsevisual.client.effect;

import com.pulsevisual.config.PulseConfig;
import com.pulsevisual.util.ColorUtil;
import net.minecraft.client.gui.DrawContext;

public class HitEffectHandler {

    private static float flashAlpha = 0f;
    private static float overlayAlpha = 0f;

    /** Call when player takes damage */
    public static void onDamage(float amount) {
        float intensity = Math.min(1f, amount / 10f);
        flashAlpha = Math.min(1f, flashAlpha + 0.6f * intensity + 0.2f);
        overlayAlpha = Math.min(0.6f, overlayAlpha + 0.4f * intensity + 0.15f);
    }

    /** Call every render tick */
    public static void tick(float delta) {
        if (flashAlpha > 0f) {
            flashAlpha = Math.max(0f, flashAlpha - delta * 0.08f);
        }
        if (overlayAlpha > 0f) {
            overlayAlpha = Math.max(0f, overlayAlpha - delta * 0.025f);
        }
    }

    /** Render onto screen */
    public static void render(DrawContext ctx, int screenW, int screenH) {
        PulseConfig cfg = PulseConfig.get();

        // Hit flash (brief bright flash)
        if (cfg.damageOverlay && flashAlpha > 0.01f) {
            int flashColor = ColorUtil.withAlpha(cfg.hitFlashColor, flashAlpha * 0.7f);
            ctx.fill(0, 0, screenW, screenH, flashColor);
        }

        // Persistent overlay (vignette-like red)
        if (cfg.damageOverlay && overlayAlpha > 0.01f) {
            int overlayColor = ColorUtil.withAlpha(0xFFCC0000, overlayAlpha * 0.4f);
            // Draw vignette edges
            int edgeW = screenW / 5;
            int edgeH = screenH / 5;
            ctx.fillGradient(0, 0, edgeW, screenH, overlayColor, 0x00000000);
            ctx.fillGradient(screenW - edgeW, 0, screenW, screenH, 0x00000000, overlayColor);
            ctx.fillGradient(0, 0, screenW, edgeH, overlayColor, 0x00000000);
            ctx.fillGradient(0, screenH - edgeH, screenW, screenH, 0x00000000, overlayColor);
        }
    }

    public static boolean isActive() {
        return flashAlpha > 0.01f || overlayAlpha > 0.01f;
    }
}
