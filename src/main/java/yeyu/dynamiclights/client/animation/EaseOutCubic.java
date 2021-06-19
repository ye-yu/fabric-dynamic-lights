package yeyu.dynamiclights.client.animation;

import net.minecraft.util.math.MathHelper;

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

    public float animate() {
        current += factor;
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

    public boolean isTargetDifferent(float newTarget) {
        return MathHelper.approximatelyEquals(target, newTarget);
    }

    public static float instance(float at) {
        return (float) (1 - Math.pow(1 - at, 3));
    }
}
