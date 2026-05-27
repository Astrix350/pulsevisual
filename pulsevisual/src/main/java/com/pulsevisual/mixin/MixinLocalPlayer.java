package com.pulsevisual.mixin;

import com.pulsevisual.config.PulseConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinLocalPlayer {

    /**
     * Tick hook — apply fullbright by giving player permanent high Night Vision.
     * We use status effect manipulation here for the cleanest result.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity)(Object)this;
        PulseConfig cfg = PulseConfig.get();

        if (cfg.fullBright) {
            // Give infinite night vision at amplifier 0 (just bright, not too washed out)
            if (!self.hasStatusEffect(StatusEffects.NIGHT_VISION) ||
                    self.getStatusEffect(StatusEffects.NIGHT_VISION).getDuration() < 220) {
                self.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION, 300, 0, false, false, false));
            }
        }

        // Night Vision Tweak: if player HAS night vision, upgrade it (no flickering at end)
        if (cfg.nightVisionTweak && !cfg.fullBright) {
            StatusEffectInstance nv = self.getStatusEffect(StatusEffects.NIGHT_VISION);
            if (nv != null && nv.getDuration() < 220) {
                self.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION, 260, nv.getAmplifier(), false, false, false));
            }
        }
    }
}
