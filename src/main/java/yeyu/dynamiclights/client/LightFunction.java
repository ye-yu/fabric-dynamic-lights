package yeyu.dynamiclights.client;

import java.util.function.BiFunction;

public enum LightFunction implements BiFunction<Float, Float, Float> {
    QUADRATIC;

    @Override
    public Float apply(Float squaredDistance, Float maxLight) {
        final float multiplier = 1;
        final float power = 1;
        final float amp = 1;
        return (maxLight / 15) * (float) Math.min(15, 15.1 + amp + (-multiplier * (Math.pow(squaredDistance, power))));
    }
}
