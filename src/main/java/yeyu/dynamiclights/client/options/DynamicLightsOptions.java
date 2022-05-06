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
    private static int maxEntitiesTick = 3;
    // @formatter:on
    private static DynamicLightsTickDelays tickLevel = DynamicLightsTickDelays.SMOOTH;
    private static DynamicLightsPrecision precision = DynamicLightsPrecision.MINIMAL;

    public static DynamicLightsLevel getCurrentOption() {
        return currentOption;
    }

    public static String getLevelOptionName() {
        return "dynamiclights.level";
    }

    public static String getEntitiesTickOptionName() {
        return "dynamiclights.entities_tick";
    }

    public static String getPerformanceOptionName() {
        return "dynamiclights.performance";
    }

    public static String getPrecisionOptionName() {
        return "dynamiclights.precision";
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
    public static void setTickLevel(final int level) {
        tickLevel = DynamicLightsTickDelays.values()[level];
    }

    public static DynamicLightsPrecision getPrecision() {
        return precision;
    }

    public static void setPrecision(DynamicLightsPrecision precision) {
        DynamicLightsOptions.precision = precision;
    }
}
