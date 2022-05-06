package yeyu.dynamiclights.client.options;

import java.util.Locale;

public class DynamicLightsOptions {
    // @formatter:off
    private static DynamicLightsLevel dynamicLightsLevel = DynamicLightsLevel.HEAVY;
    private static int maxEntitiesTick = 3;
    // @formatter:on
    private static DynamicLightsTickDelays tickLevel = DynamicLightsTickDelays.SMOOTH;
    private static DynamicLightsPrecision precision = DynamicLightsPrecision.MINIMAL;

    public static DynamicLightsLevel getDynamicLightsLevel() {
        return dynamicLightsLevel;
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
        setLightsLevel(DynamicLightsLevel.values()[level]);
    }

    public static void setLightsLevel(DynamicLightsLevel level) {
        // TODO: if DynamicLightsLevel changed, then schedule chunk rebuild instead to reset chunk values
        dynamicLightsLevel = level;
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
        setTickLevel(DynamicLightsTickDelays.values()[level]);
    }
    public static void setTickLevel(final DynamicLightsTickDelays level) {
        tickLevel = level;
    }

    public static DynamicLightsPrecision getPrecision() {
        return precision;
    }

    public static void setPrecision(DynamicLightsPrecision precision) {
        DynamicLightsOptions.precision = precision;
    }
}
