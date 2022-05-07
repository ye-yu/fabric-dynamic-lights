package yeyu.dynamiclights.client.animation;

import net.minecraft.util.math.MathHelper;
import yeyu.dynamiclights.client.options.DynamicLightsOptions;

public class EaseOutCubic {

    public final double factor;
    private double from;
    private double current;
    private double target;

    public EaseOutCubic(double factor, double from, double target) {
        this.factor = factor;
        this.from = from;
        this.target = target;
    }

    public EaseOutCubic(double from, double target) {
        this(0.1f, from, target);
    }

    public static double instance(double at) {
        return 1 - Math.pow(1 - at, 3);
    }

    public double animate() {
        current += factor * DynamicLightsOptions.getPerformance().SKIP_EVERY;
        current = MathHelper.clamp(current, 0, 1);
        return from + (target - from) * EaseOutCubic.instance(current);
    }

    public double refreshAnimation(double newTarget) {
        if (isTargetDifferent(newTarget)) return animate();
        from = from + (target - from) * EaseOutCubic.instance(current);
        current = 0;
        target = newTarget;
        return from;
    }


    public boolean isTargetDifferent(double newTarget) {
        return MathHelper.approximatelyEquals(target, newTarget);
    }

}
