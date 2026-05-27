package com.pulsevisual.mixin;

import com.pulsevisual.client.ZoomHandler;
import com.pulsevisual.client.effect.MotionBlurHandler;
import com.pulsevisual.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow @Final MinecraftClient client;

    /**
     * Modify FOV calculation to apply zoom.
     * Hooks into getFov() return value.
     */
    @ModifyVariable(
            method = "getFov",
            at = @At("RETURN"),
            ordinal = 0
    )
    private double modifyFov(double fov) {
        PulseConfig cfg = PulseConfig.get();

        // Custom FOV base
        if (cfg.customFov && !ZoomHandler.isZooming) {
            fov = cfg.fovValue;
        }

        // Sprint FOV: if disabled, neutralise the vanilla sprint effect
        // (vanilla already handles this via the fov modifier system — we skip FOV changes here)

        // Zoom
        if (cfg.zoomEnabled && ZoomHandler.isZooming) {
            fov = fov * ZoomHandler.getZoomMultiplier();
        }

        return fov;
    }

    /**
     * Apply motion blur after the main frame is drawn.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void afterRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (PulseConfig.get().motionBlur) {
            MotionBlurHandler.applyMotionBlur(client);
        }
    }

    /**
     * Block nausea/distortion shader if configured.
     */
    @Inject(method = "loadPostProcessor", at = @At("HEAD"), cancellable = true)
    private void onLoadPostProcessor(net.minecraft.util.Identifier id, CallbackInfo ci) {
        if (PulseConfig.get().noNauseaEffect) {
            String path = id.getPath();
            if (path.contains("nausea") || path.contains("drunk") || path.contains("warp")) {
                ci.cancel();
            }
        }
    }
}
