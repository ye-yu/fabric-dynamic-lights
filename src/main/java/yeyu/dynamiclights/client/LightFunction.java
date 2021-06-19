package yeyu.dynamiclights.client;

import java.util.function.BiFunction;

public enum LightFunction implements BiFunction<Float, Float, Float> {
    QUADRATIC;

    @Override
    public Float apply(Float squaredDistance, Float maxLight) {
        return Math.max(0, Math.min((-(DynamicLightsOption.getCurrentLightMultiplier() * (squaredDistance))) + 18, 14) - (14 - maxLight));
    }
}
