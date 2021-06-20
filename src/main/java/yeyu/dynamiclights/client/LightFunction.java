package yeyu.dynamiclights.client;

import java.util.function.BiFunction;

public enum LightFunction implements BiFunction<Float, Float, Float> {
    QUADRATIC;

    @Override
    public Float apply(Float squaredDistance, Float maxLight) {
        final float multiplier = DynamicLightsOptions.getCurrentLightMultiplier();
        final float power = DynamicLightsOptions.getCurrentLightPower();
        return (maxLight / 15) * (float) Math.min(15, 15.1 + (-multiplier * (Math.pow(squaredDistance, power))));
    }
}
