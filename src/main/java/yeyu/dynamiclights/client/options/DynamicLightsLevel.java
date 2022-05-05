package yeyu.dynamiclights.client.options;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import yeyu.dynamiclights.client.DynamicLightsStorage;

public enum DynamicLightsLevel {
    OFF(10, 0f, 0),
    MINIMAL(6, .8f, 6),
    HEAVY(12, 0.8f,11),
    POWERFUL(18, 1f, 16);

    public final int RADIUS;
    public final float MAX;

    public final int FACTOR;

    DynamicLightsLevel(int radius, float max, int factor) {
        RADIUS = radius;
        MAX = max;
        FACTOR = factor;
    }

    public void iterateLightMap(Vec3d cameraPosVec, ClientWorld clientWorld, float maxLight) {
        iterateLightMap(cameraPosVec, clientWorld, maxLight, false);
    }

    public void iterateLightMap(Vec3d cameraPosVec, ClientWorld clientWorld, float maxLight, boolean force) {
        final BlockPos playerBp = new BlockPos(cameraPosVec);
        final int radius = RADIUS;
        clientWorld.getProfiler().push("queueCalculateLight");
        BlockPos.iterate(playerBp.add(-radius - 1, 0, -radius - 1), playerBp.add(radius, 0, radius)).forEach(bp -> processBlockPos(bp, cameraPosVec, clientWorld, maxLight, force));
        clientWorld.getProfiler().pop();
    }

    public double getLightFactor(final double dist) {
        return 1 - dist / this.FACTOR;
    }

    private void processBlockPos(BlockPos blockPos, Vec3d cameraPosVec, ClientWorld clientWorld, float maxLight, boolean forceOff) {
        BlockPos.Mutable mutable = (BlockPos.Mutable) blockPos;
        final int mutableY = mutable.getY();
        for(int i = mutableY - 5; i < mutableY + 5; i++) {
            if (i < -125) continue;
            mutable.setY(i);
            float maxLightMultiplier = 1f;

            final var precision = DynamicLightsOptions.getPrecision();

            if (this == DynamicLightsLevel.OFF || forceOff) {
                DynamicLightsStorage.setLightLevel(mutable, 0, true);
            } else if (precision == DynamicLightsPrecision.MINIMAL) {
                iterateLightLevelForBlockPos(cameraPosVec, maxLight, mutable, maxLightMultiplier);
            } else if (precision == DynamicLightsPrecision.ENHANCED) {
                if (i == 0) {
                    iterateLightLevelHorizontalPlane(cameraPosVec, maxLight, mutable, maxLightMultiplier);
                } else {
                    iterateLightLevelForBlockPos(cameraPosVec, maxLight, mutable, maxLightMultiplier);
                }
            } else if (precision == DynamicLightsPrecision.POWERFUL){
                iterateLightLevelHorizontalPlane(cameraPosVec, maxLight, mutable, maxLightMultiplier);
            }
        }
    }

    private void iterateLightLevelHorizontalPlane(Vec3d cameraPosVec, float maxLight, BlockPos.Mutable mutable, float maxLightMultiplier) {
        final var ox = mutable.getX();
        final var oz = mutable.getZ();
        for (int ddx = -3; ddx < 4; ddx++) {
            for (int ddz = -3; ddz < 4; ddz++) {
                mutable.setX(ox + ddx);
                mutable.setZ(oz + ddz);
                iterateLightLevelForBlockPos(cameraPosVec, maxLight, mutable, maxLightMultiplier);
            }
        }
        mutable.setX(ox);
        mutable.setZ(oz);
    }

    private void iterateLightLevelForBlockPos(Vec3d cameraPosVec, float maxLight, BlockPos.Mutable mutable, float maxLightMultiplier) {
        final double x = mutable.getX() + 0.5;
        final double y = mutable.getY() + 0.5;
        final double z = mutable.getZ() + 0.5;
        final double camX = cameraPosVec.getX();
        final double camY = cameraPosVec.getY();
        final double camZ = cameraPosVec.getZ();
        final double dx = camX - x;
        final double dy = camY - y;
        final double dz = camZ - z;
        final double dist = Math.hypot(dx, Math.hypot(dy, dz));

        final double lightFactor = this.getLightFactor(dist);
        final double maxLightFactor = MathHelper.clamp(lightFactor, 0, this.MAX);

        final double lightLevel = MathHelper.clamp(maxLightMultiplier * maxLight * maxLightFactor, 0, 15);
        DynamicLightsStorage.setLightLevel(mutable, lightLevel, false);
    }
}
