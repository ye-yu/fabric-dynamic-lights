package yeyu.dynamiclights.client.options;

import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.Option;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;

import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DynamicLightsOptions {
    // @formatter:off
    private static DynamicLightsLevel currentOption = DynamicLightsLevel.HEAVY;
    public static final Pair<String, Option> DYNAMIC_LIGHTS_OPTIONS = new Pair<>("dynamiclights.level", (new DoubleOption("dynamiclights.level",
            0.0D,
            DynamicLightsLevel.values().length - 1,
            1.0F,
            $ -> (double) currentOption.ordinal(),
            (gameOptions, mipmapLevels) -> setLightsLevel((int) (double) mipmapLevels),
            ($, option) -> {
                String d = getCurrentOption().name();
                return new TranslatableText("options.generic_value", new TranslatableText("dynamiclights.level"), d);
            },
            (client) -> client.textRenderer.wrapLines(new TranslatableText("dynamiclights.level.desc"), 200))));
    private static int maxEntitiesTick = 3;
    public static final Pair<String, Option> DYNAMIC_LIGHTS_ENTITIES = new Pair<>("dynamiclights.entities_tick",
            new DoubleOption("dynamiclights.entities_tick",
                    4,
                    50,
                    2,
                    $ -> (double) maxEntitiesTick,
                    ($, value) -> setMaxEntitiesToTick(value.intValue()),
                    ($, option) -> {
                        int d = getMaxEntitiesToTick();
                        return new TranslatableText("options.generic_value", new TranslatableText("dynamiclights.entities_tick"), d);
                    },
                    (client) -> client.textRenderer.wrapLines(new TranslatableText("dynamiclights.entities_tick.desc"), 200)));
    // @formatter:on
    private static DynamicLightsTickDelays tickLevel = DynamicLightsTickDelays.SMOOTH;
    public static final Pair<String, Option> DYNAMIC_LIGHTS_PERFORMANCE = new Pair<>("dynamiclights.performance",
            CyclingOption.create("dynamiclights.performance",
                    () -> IntStream.range(0, DynamicLightsTickDelays.values().length).boxed().collect(Collectors.toList()),
                    (level) -> new LiteralText(DynamicLightsTickDelays.values()[level].name()),
                    ($) -> getTickLevel().ordinal(),
                    ($, $$, level) -> tickLevel = DynamicLightsTickDelays.values()[level]));
    private static DynamicLightsPrecision precision = DynamicLightsPrecision.MINIMAL;
    public static final Pair<String, Option> DYNAMIC_LIGHTS_PRECISION = new Pair<>("dynamiclights.precision",
            CyclingOption.create("dynamiclights.precision",
                    () -> IntStream.range(0, DynamicLightsPrecision.values().length).boxed().collect(Collectors.toList()),
                    (level) -> new LiteralText(DynamicLightsPrecision.values()[level].name()),
                    ($) -> getPrecision().ordinal(),
                    ($, $$, level) -> precision = DynamicLightsPrecision.values()[level]));
    public static final Option[] OPTIONS = new Option[]{
            DYNAMIC_LIGHTS_ENTITIES.getRight(),
            DYNAMIC_LIGHTS_OPTIONS.getRight(),
            DYNAMIC_LIGHTS_PERFORMANCE.getRight(),
            DYNAMIC_LIGHTS_PRECISION.getRight(),
    };

    public static DynamicLightsLevel getCurrentOption() {
        return currentOption;
    }

    public static void setLightsLevel(int level) {
        // TODO: if DynamicLightsLevel changed, then schedule chunk rebuild instead to reset chunk values
        currentOption = DynamicLightsLevel.values()[level];
    }

    public static int getMaxEntitiesToTick() {
        return maxEntitiesTick;
    }

    public static void setMaxEntitiesToTick(int max) {
        maxEntitiesTick = max;
    }

    public static DynamicLightsTickDelays getTickLevel() {
        return tickLevel;
    }

    public static void setTickLevel(final String level) {
        tickLevel = DynamicLightsTickDelays.STR2OBJ.get().getOrDefault(level.toUpperCase(Locale.US), DynamicLightsTickDelays.EASE);
    }

    public static DynamicLightsPrecision getPrecision() {
        return precision;
    }

    public static void setPrecision(DynamicLightsPrecision precision) {
        DynamicLightsOptions.precision = precision;
    }
}
