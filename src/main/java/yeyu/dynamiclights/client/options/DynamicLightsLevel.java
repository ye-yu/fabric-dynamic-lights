package yeyu.dynamiclights.client.options;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import yeyu.dynamiclights.client.DynamicLightsStorage;

public enum DynamicLightsLevel {
    OFF(10, 0f),
    MINIMAL(6, .8f),
    HEAVY(12, 0.8f),
    POWERFUL(18, 1f);

    public final int RADIUS;
    public final float MAX;

    DynamicLightsLevel(int radius, float max) {
        RADIUS = radius;
        MAX = max;
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

    private void processBlockPos(BlockPos blockPos, Vec3d cameraPosVec, ClientWorld clientWorld, float maxLight, boolean forceOff) {
        BlockPos.Mutable mutable = (BlockPos.Mutable) blockPos;
        final int mutableY = mutable.getY();
        final long timeOfDay = clientWorld.getTimeOfDay();
        for(int i = mutableY - 5; i < mutableY + 5; i++) {
            if (i < -125) continue;
            mutable.setY(i);
            float maxLightMultiplier = 1f;

            if (clientWorld.getLightLevel(LightType.SKY, mutable) > maxLight) {
                if (timeOfDay < 12000) forceOff = true;
                else if (timeOfDay < 13000) {
                    maxLightMultiplier = 1 - (13000.0f - timeOfDay) / 1000;
                } else if (timeOfDay > 23000) {
                    maxLightMultiplier = (24000.0f - timeOfDay) / 1000;
                }
            }

            final var precision = DynamicLightsOptions.getPrecision();

            if (this == DynamicLightsLevel.OFF || forceOff) {
                if (!DynamicLightsStorage.setLightLevel(mutable, 0, true)) continue;
                // do not check blocks that has been scheduled;
                DynamicLightsStorage.BP_UPDATED.putIfAbsent(mutable.asLong(), true);
            } else if (precision == DynamicLightsPrecision.MINIMAL) {
                iterateLightLevelForBlockPos(cameraPosVec, maxLight, mutable, maxLightMultiplier);
            } else if (precision == DynamicLightsPrecision.ENHANCED) {
                if (i == 0) {
                    iterateLightLevelHorizontalPlane(cameraPosVec, maxLight, mutable, maxLightMultiplier);
                } else {
                    iterateLightLevelForBlockPos(cameraPosVec, maxLight, mutable, maxLightMultiplier);
                }
            }else if (precision == DynamicLightsPrecision.POWERFUL){
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
        final float x = mutable.getX() + 0.5f;
        final float y = mutable.getY() + 0.5f;
        final float z = mutable.getZ() + 0.5f;
        final float camX = (float) cameraPosVec.getX();
        final float camY = (float) cameraPosVec.getY();
        final float camZ = (float) cameraPosVec.getZ();
        final float dx = camX - x;
        final float dy = camY - y;
        final float dz = camZ - z;
        final float dist = dx * dx + dy * dy + dz * dz;

        final float maxLightFactor =
                this == DynamicLightsLevel.POWERFUL ? Math.max(0, Math.min(1, dist * -0.03f + 1.08f))
                : Math.max(0, Math.min(1, dist * -0.24f + 1.1f));
        final double lightLevel = MathHelper.clamp(maxLightMultiplier * maxLight * maxLightFactor, 0, 15);
        if (!DynamicLightsStorage.setLightLevel(mutable, lightLevel, false)) return;
        // do not check blocks that has been scheduled;
        DynamicLightsStorage.BP_UPDATED.putIfAbsent(mutable.asLong(), true);
    }
}
