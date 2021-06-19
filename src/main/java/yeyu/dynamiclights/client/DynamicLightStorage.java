package yeyu.dynamiclights.client;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import yeyu.dynamiclights.client.animation.EaseOutCubic;

import java.util.HashMap;
import java.util.Map;

public class DynamicLightStorage {
    public static Map<Long, Double> BP_TO_LIGHT_LEVEL = new HashMap<>();
    public static Map<Integer, EaseOutCubic> LIGHT_ANIMATE_INSTANCE = new HashMap<>();
    public static Map<Integer, Boolean> LIGHT_ANIMATE_STATUS = new HashMap<>();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean setLightLevel(BlockPos bp, double lightLevel, boolean force) {
        final long bpLong = bp.asLong();
        if (force) {
            final Double previous = BP_TO_LIGHT_LEVEL.put(bpLong, lightLevel);
            return previous != null && !MathHelper.approximatelyEquals(previous, lightLevel);
        }
        if (lightLevel < 1e-5) {
            return BP_TO_LIGHT_LEVEL.remove(bpLong) != null;
        } else {
            final Double current = BP_TO_LIGHT_LEVEL.getOrDefault(bpLong, .0);
            if (current > lightLevel) return false;
            BP_TO_LIGHT_LEVEL.put(bpLong, lightLevel);
            return true;
        }
    }

    public static double getLightLevel(BlockPos bp) {
        return BP_TO_LIGHT_LEVEL.getOrDefault(bp.asLong(), .0);
    }

    public static float animationFactor(Entity entity, float newLight) {
        final int entityId = entity.getId();
        final EaseOutCubic easeOutCubic = LIGHT_ANIMATE_INSTANCE.computeIfAbsent(entityId, $ -> new EaseOutCubic(0, newLight));
        return easeOutCubic.refreshAnimation(newLight);
    }
}