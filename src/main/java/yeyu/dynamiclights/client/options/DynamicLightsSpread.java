package yeyu.dynamiclights.client.options;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import yeyu.dynamiclights.client.DynamicLightsStorage;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

public enum DynamicLightsSpread {
    NONE(8, 1) {
        @Override
        public void computeDynamicLights(long origin, double originX, double originY, double originZ, double maxLight, Predicate<Long> cannotAddNewLight, Consumer<Long> onBpChange) {
            final int x = BlockPos.unpackLongX(origin);
            final int y = BlockPos.unpackLongY(origin);
            final int z = BlockPos.unpackLongZ(origin);
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                for (int dy = -(RADIUS / 2); dy <= (RADIUS / 2); dy++) {
                    for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                        final int blockX = x + dx;
                        final int blockY = y + dy;
                        final int blockZ = z + dz;
                        final double lightLevel = MathHelper.clamp(0, 0, 15);
                        final long bpLong = BlockPos.asLong(blockX, blockY, blockZ);
                        final boolean previousLoopRoundHasAddedLight = cannotAddNewLight.test(bpLong);
                        onBpChange.accept(bpLong);
                        if (!previousLoopRoundHasAddedLight) {
                            DynamicLightsStorage.BP_TO_LIGHT_LEVEL.put(bpLong, lightLevel);
                        } else {
                            DynamicLightsStorage.BP_TO_LIGHT_LEVEL.merge(bpLong, lightLevel, Math::max);
                        }
                    }
                }
            }
        }
    },
    MEDIUM(5, .1),
    LARGE(8, .1),
    ;

    public final int RADIUS;
    public final double FACTOR;

    public static final HashMap<String, DynamicLightsSpread> STR2OBJ = new HashMap<>() {{
        for (DynamicLightsSpread value : DynamicLightsSpread.values()) {
            put(value.toString(), value);
        }
    }};

    DynamicLightsSpread(int radius, double factor) {
        RADIUS = radius;
        FACTOR = factor;
    }

    public void computeDynamicLights(long origin, double originX, double originY, double originZ, double maxLight, Predicate<Long> cannotAddNewLight, Consumer<Long> onBpChange) {
        final int x = BlockPos.unpackLongX(origin);
        final int y = BlockPos.unpackLongY(origin);
        final int z = BlockPos.unpackLongZ(origin);
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -(RADIUS / 2); dy <= (RADIUS / 2); dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    final int blockX = x + dx;
                    final int blockY = y + dy;
                    final int blockZ = z + dz;
                    final double targetX = blockX + .5;
                    final double targetY = blockY + .5;
                    final double targetZ = blockZ + .5;
                    final double distX = originX - targetX;
                    final double distY = originY - targetY;
                    final double distZ = originZ - targetZ;
                    final double dist = Math.hypot(distX, Math.hypot(distY, distZ));
                    final double lightFactor = (1 - dist * FACTOR);
                    final double lightLevel = MathHelper.clamp(maxLight * lightFactor, 0, 15);

                    final long bpLong = BlockPos.asLong(blockX, blockY, blockZ);
                    final boolean previousLoopRoundHasAddedLight = cannotAddNewLight.test(bpLong);
                    onBpChange.accept(bpLong);
                    if (!previousLoopRoundHasAddedLight) {
                        DynamicLightsStorage.BP_TO_LIGHT_LEVEL.put(bpLong, lightLevel);
                    } else {
                        DynamicLightsStorage.BP_TO_LIGHT_LEVEL.merge(bpLong, lightLevel, Math::max);
                    }
                }
            }
        }
    }

    public void computeLightsOff(long origin, Predicate<Long> cannotTurnOffLight, Consumer<Long> onBpChange) {
        final int x = BlockPos.unpackLongX(origin);
        final int y = BlockPos.unpackLongY(origin);
        final int z = BlockPos.unpackLongZ(origin);
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -(RADIUS / 2); dy <= (RADIUS / 2); dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    final int blockX = x + dx;
                    final int blockY = y + dy;
                    final int blockZ = z + dz;
                    final long bpLong = BlockPos.asLong(blockX, blockY, blockZ);
                    if (cannotTurnOffLight.test(bpLong)) continue;
                    onBpChange.accept(bpLong);
                    DynamicLightsStorage.BP_TO_LIGHT_LEVEL.remove(bpLong);
                }
            }
        }
    }

    public static void clearFromCenter(long origin) {
        final int RADIUS = DynamicLightsSpread.LARGE.RADIUS;
        final int x = BlockPos.unpackLongX(origin);
        final int y = BlockPos.unpackLongY(origin);
        final int z = BlockPos.unpackLongZ(origin);
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -(RADIUS / 2); dy <= (RADIUS / 2); dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    final int blockX = x + dx;
                    final int blockY = y + dy;
                    final int blockZ = z + dz;
                    final long bpLong = BlockPos.asLong(blockX, blockY, blockZ);
                    DynamicLightsStorage.BP_TO_LIGHT_LEVEL.remove(bpLong);
                }
            }
        }

    }

    @Override
    public String toString() {
        return this.name().toUpperCase(Locale.US);
    }
}
