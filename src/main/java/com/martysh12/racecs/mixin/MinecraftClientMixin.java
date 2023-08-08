package com.martysh12.racecs.mixin;

import com.martysh12.racecs.RaceCS;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("TAIL"), method = "<init>")
    void onInit(CallbackInfo callbackInfo) {
        RaceCS.INSTANCE.onInitializeClient();
    }
}
