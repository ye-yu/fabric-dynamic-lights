package yeyu.dynamiclights.mixin.option;

import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.Option;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import yeyu.dynamiclights.client.DynamicLightsOption;

import java.util.Arrays;

@Mixin(VideoOptionsScreen.class)
public class VideoOptionsScreenMixin {
    @Mutable
    @Shadow
    @Final
    private static Option[] OPTIONS;

    static {
        final Option[] options = Arrays.copyOf(OPTIONS, OPTIONS.length + 1);
        options[options.length - 1] = DynamicLightsOption.DYNAMIC_LIGHTS_OPTION;
        OPTIONS = options;
    }
}
