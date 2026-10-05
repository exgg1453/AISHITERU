package com.aishiteru.mixin;

import com.aishiteru.AishiteruManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "render(F)V", at = @At("TAIL"))
    private void aishiteru$onRender(float tickDelta, CallbackInfo callbackInfo) {
        AishiteruManager.onHudRender(MinecraftClient.getInstance());
    }
}
