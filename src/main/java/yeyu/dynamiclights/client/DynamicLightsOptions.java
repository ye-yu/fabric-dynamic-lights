package yeyu.dynamiclights.client;

import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.Option;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;

public class DynamicLightsOptions {
    private static DynamicLightsLevel currentOption = DynamicLightsLevel.THREE;
    private static int maxEntitiesTick = 4;

    public static final Pair<String, Option> DYNAMIC_LIGHTS_OPTIONS = new Pair<>("dynamiclights.level", (new DoubleOption("dynamiclights.level",
            0.0D,
            DynamicLightsLevel.values().length - 1,
            1.0F,
            $ -> (double) currentOption.ordinal(),
            (gameOptions, mipmapLevels) -> setCurrentOption((int) (double) mipmapLevels),
            ($, option) -> {
                int d = getCurrentOption().ordinal();
                return new TranslatableText("options.generic_value", new TranslatableText("dynamiclights.level"), d);
            },
            (client) -> client.textRenderer.wrapLines(new TranslatableText("dynamiclights.level.desc"), 200))));

    public static final Pair<String, Option> DYNAMIC_LIGHTS_ENTITIES = new Pair<>("dynamiclights.entities_tick",
            new DoubleOption("dynamiclights.entities_tick",
                    4,
                    50,
                    2,
                    $ -> (double) maxEntitiesTick,
                    ($, value) -> setMaxEntitiesToTick((int) (double) value),
                    ($, option) -> {
                        int d = getMaxEntitiesToTick();
                        return new TranslatableText("options.generic_value", new TranslatableText("dynamiclights.entities_tick"), d);
                    },
                    (client) -> client.textRenderer.wrapLines(new TranslatableText("dynamiclights.entities_tick.desc"), 200)));

    public static final Option[] OPTIONS = new Option[]{
            DYNAMIC_LIGHTS_ENTITIES.getRight(),
            DYNAMIC_LIGHTS_OPTIONS.getRight()
    };
    
    public final String optionName;
    public final Option optionUI;
    
    DynamicLightsOptions(String optionName, Option optionUI) {
        this.optionName = optionName;
        this.optionUI = optionUI;
    }

    public static DynamicLightsLevel getCurrentOption() {
        return currentOption;
    }

    public static void setCurrentOption(int level) {
        currentOption = DynamicLightsLevel.values()[level];
    }

    public static float getCurrentLightMultiplier() {
        return currentOption.MULTIPLIER;
    }

    public static float getCurrentLightPower() {
        return currentOption.POWER;
    }

    public static void setMaxEntitiesToTick(int max) {
        maxEntitiesTick = max;
    }
    public static int getMaxEntitiesToTick() {
        return maxEntitiesTick;
    }

}
