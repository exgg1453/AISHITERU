package com.aishiteru.mixin;

import com.aishiteru.AishiteruManager;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "tick()V", at = @At("TAIL"))
    private void aishiteru$onTick(CallbackInfo callbackInfo) {
        AishiteruManager.onClientTick((MinecraftClient) (Object) this);
    }
}
