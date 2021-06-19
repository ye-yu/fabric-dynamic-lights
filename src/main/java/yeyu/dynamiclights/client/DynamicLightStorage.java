package yeyu.dynamiclights.client;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class DynamicLightStorage {
    public static Map<Long, Double> BP_TO_LIGHT_LEVEL = new HashMap<>();

    public static boolean setLightLevel(BlockPos bp, double lightLevel) {
        final long bpLong = bp.asLong();
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
}