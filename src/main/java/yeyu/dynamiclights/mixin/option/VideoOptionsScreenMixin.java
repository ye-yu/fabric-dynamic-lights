package yeyu.dynamiclights.mixin.option;

import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yeyu.dynamiclights.client.options.DynamicLightsWidget;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(VideoOptionsScreen.class)
public class VideoOptionsScreenMixin {

    @Inject(method = "getOptions", at = @At("RETURN"), cancellable = true)
    private static void getOptions(GameOptions gameOptions, CallbackInfoReturnable<SimpleOption<?>[]> cir) {
        final ArrayList<SimpleOption<?>> optionsArray = new ArrayList<>(Arrays.asList(cir.getReturnValue()));
        optionsArray.addAll(DynamicLightsWidget.INSTANCE.OPTIONS);

        final SimpleOption<?>[] simpleOptions = optionsArray.toArray(SimpleOption<?>[]::new);
        cir.setReturnValue(simpleOptions);
    }
}
