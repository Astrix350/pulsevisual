package com.pulsevisual.mixin;

import com.pulsevisual.client.effect.HitEffectHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(net.minecraft.server.world.ServerWorld world,
                          DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && self == mc.player) {
            HitEffectHandler.onDamage(amount);
        }
    }
}
