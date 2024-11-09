package com.martysh12.racecs.mixin;

import com.martysh12.racecs.net.StationManager;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin {
    @Inject(at = @At("TAIL"), method = "setLanguage")
    private void onSetLanguage(CallbackInfo callbackInfo) {
        StationManager.downloadStations();
    }
}
