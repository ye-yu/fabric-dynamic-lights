package yeyu.dynamiclights.client.options;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Range;
import yeyu.dynamiclights.client.DynamicLightsDebug;
import yeyu.dynamiclights.client.DynamicLightsStorage;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public enum DynamicLightsSpread {
    SMALL(3),
    MEDIUM(6),
    LARGE(9),
    ;

    public final int RADIUS;
    public final double FACTOR;

    public static final HashMap<String, DynamicLightsSpread> STR2OBJ = new HashMap<>() {{
        for (DynamicLightsSpread value : DynamicLightsSpread.values()) {
            put(value.toString(), value);
        }
    }};

    DynamicLightsSpread(@Range(from = 1, to = Integer.MAX_VALUE) int radius) {
        RADIUS = radius;
        FACTOR = 1d / radius;
    }

    private static final DynamicLightsDebug debug = new DynamicLightsDebug(4);

    public void computeDynamicLights(long origin, double originX, double originY, double originZ, double maxLight, boolean replaceExistingOnly, Consumer<Long> onBpChange) {
        final int x = BlockPos.unpackLongX(origin);
        final int y = BlockPos.unpackLongY(origin);
        final int z = BlockPos.unpackLongZ(origin);
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    final int blockX = x + dx;
                    final int blockY = y + dy;
                    final int blockZ = z + dz;
                    final double targetX = blockX + .5;
                    final double targetY = blockY + .5;
                    final double targetZ = blockZ + .5;
                    final double distX = (originX - targetX) * .8;
                    final double distY = (originY - targetY) * .8;
                    final double distZ = (originZ - targetZ) * .8;
                    final double dist = Math.hypot(distX, Math.hypot(distY, distZ));


                    final double lightFactor = 1 - dist * FACTOR;
                    final double lightLevel = MathHelper.clamp(maxLight * lightFactor, 0, 15);

                    if (dx == 0 && dy == 0 && dz == 0) {
                        debug.change(
                                dist,
                                RADIUS,
                                lightFactor,
                                lightLevel);
                    }

                    if (replaceExistingOnly) {
                        DynamicLightsStorage.BP_TO_LIGHT_LEVEL.computeIfPresent(
                                BlockPos.asLong(blockX, blockY, blockZ),
                                (bp, value) -> {
                                    onBpChange.accept(bp);
                                    return Math.max(value, lightLevel);
                                }
                        );

                    } else {
                        final long bpLong = BlockPos.asLong(blockX, blockY, blockZ);
                        onBpChange.accept(bpLong);
                        DynamicLightsStorage.BP_TO_LIGHT_LEVEL.merge(
                                bpLong,
                                lightLevel,
                                Math::max
                        );

                    }
                }
            }
        }
    }

    public void computeLightsOff(long origin, Predicate<Long> cannotTurnOffLight) {
        final int x = BlockPos.unpackLongX(origin);
        final int y = BlockPos.unpackLongY(origin);
        final int z = BlockPos.unpackLongZ(origin);
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    final int blockX = x + dx;
                    final int blockY = y + dy;
                    final int blockZ = z + dz;
                    final long bpLong = BlockPos.asLong(blockX, blockY, blockZ);
                    if (cannotTurnOffLight.test(bpLong)) continue;
                    DynamicLightsStorage.BP_TO_LIGHT_LEVEL.put(bpLong, 0d);
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.name().toUpperCase(Locale.US);
    }
}
