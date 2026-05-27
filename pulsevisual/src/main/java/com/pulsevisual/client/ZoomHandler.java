package com.pulsevisual.client;

import com.pulsevisual.config.PulseConfig;

public class ZoomHandler {

    public static boolean isZooming = false;
    private static float currentZoom = 1.0f;
    private static float targetZoom = 1.0f;
    private static float scrollDelta = 0f;

    /** Called from FOV mixin — returns multiplier to apply to FOV */
    public static float getZoomMultiplier() {
        if (!PulseConfig.get().zoomEnabled) return 1.0f;
        return 1.0f / currentZoom;
    }

    /** Tick smooth interpolation — call every client tick */
    public static void tick() {
        if (!PulseConfig.get().zoomEnabled) {
            currentZoom = 1.0f;
            return;
        }

        if (isZooming) {
            targetZoom = PulseConfig.get().zoomLevel + scrollDelta;
            targetZoom = Math.max(1.5f, Math.min(16f, targetZoom));
        } else {
            targetZoom = 1.0f;
        }

        if (PulseConfig.get().smoothZoom) {
            currentZoom += (targetZoom - currentZoom) * 0.15f;
            if (Math.abs(currentZoom - targetZoom) < 0.001f) currentZoom = targetZoom;
        } else {
            currentZoom = targetZoom;
        }
    }

    public static void onScroll(double delta) {
        if (isZooming) {
            scrollDelta += (float)(delta * 0.5);
            scrollDelta = Math.max(-3f, Math.min(8f, scrollDelta));
        }
    }

    public static void onZoomKeyPressed() {
        isZooming = true;
    }

    public static void onZoomKeyReleased() {
        isZooming = false;
        scrollDelta = 0f;
    }

    public static float getCurrentZoom() { return currentZoom; }
}
