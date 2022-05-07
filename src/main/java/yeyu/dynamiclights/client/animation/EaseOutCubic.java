package yeyu.dynamiclights.client.animation;

import net.minecraft.util.math.MathHelper;
import yeyu.dynamiclights.client.options.DynamicLightsOptions;

public class EaseOutCubic {

    public final float factor;
    private float from;
    private float current;
    private float target;

    public EaseOutCubic(float factor, float from, float target) {
        this.factor = factor;
        this.from = from;
        this.target = target;
    }

    public EaseOutCubic(float from, float target) {
        this(0.1f, from, target);
    }

    public static float instance(float at) {
        return (float) (1 - Math.pow(1 - at, 3));
    }

    public float animate() {
        current += factor * DynamicLightsOptions.getPerformance().SKIP_EVERY;
        current = MathHelper.clamp(current, 0, 1);
        return from + (target - from) * EaseOutCubic.instance(current);
    }

    public float refreshAnimation(float newTarget) {
        if (isTargetDifferent(newTarget)) return animate();
        from = from + (target - from) * EaseOutCubic.instance(current);
        current = 0;
        target = newTarget;
        return from;
    }

    public void overwriteFrom(float from) {
        this.from = this.target = from;
        current = 1;
    }


    public boolean isTargetDifferent(float newTarget) {
        return MathHelper.approximatelyEquals(target, newTarget);
    }

}
