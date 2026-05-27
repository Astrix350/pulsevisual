package com.pulsevisual.client.effect;

import com.pulsevisual.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

/**
 * Software motion-blur using framebuffer accumulation.
 * Blend current frame with last frame by `strength` factor.
 */
public class MotionBlurHandler {

    private static Framebuffer accumulationBuffer = null;
    private static int lastWidth = -1;
    private static int lastHeight = -1;

    public static void applyMotionBlur(MinecraftClient mc) {
        if (!PulseConfig.get().motionBlur) return;

        int w = mc.getFramebuffer().textureWidth;
        int h = mc.getFramebuffer().textureHeight;

        if (accumulationBuffer == null || lastWidth != w || lastHeight != h) {
            if (accumulationBuffer != null) accumulationBuffer.delete();
            accumulationBuffer = new SimpleFramebuffer(w, h, true, MinecraftClient.IS_SYSTEM_MAC);
            lastWidth = w;
            lastHeight = h;
        }

        float strength = PulseConfig.get().motionBlurStrength;
        strength = Math.max(0.1f, Math.min(0.95f, strength));

        // Blit current → accumulation (blend)
        Framebuffer main = mc.getFramebuffer();

        // Read current frame
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, main.fbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, accumulationBuffer.fbo);

        // Enable blending for accumulation
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE);

        GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h,
                GL11.GL_COLOR_BUFFER_BIT, GL11.GL_LINEAR);

        GL11.glDisable(GL11.GL_BLEND);

        // Blit accumulation → main
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, accumulationBuffer.fbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, main.fbo);
        GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h,
                GL11.GL_COLOR_BUFFER_BIT, GL11.GL_LINEAR);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, main.fbo);
    }

    public static void cleanup() {
        if (accumulationBuffer != null) {
            accumulationBuffer.delete();
            accumulationBuffer = null;
        }
    }
}
