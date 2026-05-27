package com.pulsevisual.mixin;

import com.pulsevisual.client.ZoomHandler;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouseHandler {

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ZoomHandler.isZooming) {
            ZoomHandler.onScroll(vertical);
            ci.cancel(); // don't process scroll as item switch while zooming
        }
    }
}
