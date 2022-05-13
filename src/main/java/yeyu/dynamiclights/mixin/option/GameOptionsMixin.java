package yeyu.dynamiclights.mixin.option;

import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yeyu.dynamiclights.client.options.DynamicLightsOptions;

import java.io.IOException;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Inject(method = "write", at = @At("HEAD"))
    private void injectHeadWrite(CallbackInfo ci) throws IOException {
        DynamicLightsOptions.writeSettings();
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void injectHeadLoad(CallbackInfo ci) throws IOException {
        DynamicLightsOptions.readSettings();
    }
}
