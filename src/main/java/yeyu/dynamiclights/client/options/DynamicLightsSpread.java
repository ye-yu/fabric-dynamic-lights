package yeyu.dynamiclights.client.options;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import yeyu.dynamiclights.SerializableEnum;
import yeyu.dynamiclights.client.DynamicLightsStorage;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

public enum DynamicLightsSpread implements SerializableEnum<DynamicLightsSpread> {
    OFF(8, 1) {
        @Override
        public void computeDynamicLights(long origin, double originX, double originY, double originZ, double maxLight, Predicate<Long> cannotAddNewLight, Consumer<Long> onBpChange) {
        }

        @Override
        public void computeLightsOff(long origin, Predicate<Long> cannotTurnOffLight, Consumer<Long> onBpChange) {
        }
    },
    FAST(5, .1),
    FANCY(8, .1),
    ;

    public static final HashMap<String, DynamicLightsSpread> STR2OBJ = new HashMap<>() {{
        for (DynamicLightsSpread value : DynamicLightsSpread.values()) {
            put(value.toString(), value);
        }
    }};
    public final int RADIUS;
    public final double FACTOR;

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

    @Override
    public String toString() {
        return this.name().toUpperCase(Locale.US);
    }

    @Override
    public DynamicLightsSpread[] getValues() {
        return DynamicLightsSpread.values();
    }
}
