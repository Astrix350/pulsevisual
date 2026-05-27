package com.pulsevisual.mixin;

import com.pulsevisual.client.hud.PulseHudRenderer;
import com.pulsevisual.config.PulseConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    /** Inject custom PulseHUD after vanilla HUD finishes rendering */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (!PulseConfig.get().hudEnabled) return;
        int sw = context.getScaledWindowWidth();
        int sh = context.getScaledWindowHeight();
        PulseHudRenderer.render(context, sw, sh, tickDelta);
    }

    /** Suppress vanilla health bar when our custom one is enabled */
    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void suppressHealthBar(DrawContext ctx, CallbackInfo ci) {
        if (PulseConfig.get().hudEnabled && PulseConfig.get().showHealth) {
            ci.cancel();
        }
    }

    /** Suppress vanilla armor bar */
    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void suppressStatusBars(DrawContext ctx, CallbackInfo ci) {
        if (PulseConfig.get().hudEnabled && PulseConfig.get().showArmor) {
            // We cancel the full status bar section and render our own
            ci.cancel();
        }
    }

    /** Suppress vanilla experience bar */
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void suppressXpBar(DrawContext ctx, int x, CallbackInfo ci) {
        if (PulseConfig.get().hudEnabled && PulseConfig.get().showExperience) {
            ci.cancel();
        }
    }

    /** Suppress vanilla crosshair when custom is active */
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void suppressCrosshair(DrawContext ctx, CallbackInfo ci) {
        if (PulseConfig.get().hudEnabled && PulseConfig.get().customCrosshair) {
            ci.cancel();
        }
    }
}
