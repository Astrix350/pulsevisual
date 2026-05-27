package com.pulsevisual.mixin;

import com.pulsevisual.config.PulseConfig;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCameraSubmersionType {

    /** Used for pumpkin blur removal — block POWDER_SNOW submersion overlay */
    @Inject(method = "getSubmersionType", at = @At("RETURN"), cancellable = true)
    private void modifySubmersion(CallbackInfoReturnable<CameraSubmersionType> cir) {
        if (PulseConfig.get().noPumpkinBlur) {
            if (cir.getReturnValue() == CameraSubmersionType.POWDER_SNOW) {
                cir.setReturnValue(CameraSubmersionType.NONE);
            }
        }
    }
}
