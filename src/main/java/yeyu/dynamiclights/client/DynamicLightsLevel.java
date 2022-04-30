package yeyu.dynamiclights.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public enum DynamicLightsLevel {
    OFF(10, 0f, 0f, 0),
    ONE(4, 0.135f, 2.57f, 0),
    TWO(5, 0.44f, 1.43f, .1f);

    public final int RADIUS;
    public final float MULTIPLIER;
    public final float POWER;
    public final float AMP;

    DynamicLightsLevel(int radius, float multiplier, float power, float amp) {
        RADIUS = radius;
        MULTIPLIER = multiplier;
        POWER = power;
        AMP = amp;
    }

    void iterateLightMap(Vec3d cameraPosVec, ClientWorld clientWorld, float maxLight) {
        iterateLightMap(cameraPosVec, clientWorld, maxLight, false);
    }

    void iterateLightMap(Vec3d cameraPosVec, ClientWorld clientWorld, float maxLight, boolean force) {
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
            if (i < 0) continue;
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

            if (this == DynamicLightsLevel.OFF || forceOff) {
                if (!DynamicLightsStorage.setLightLevel(mutable, 0, true)) continue;
            } else {
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
                final double lightLevel = maxLightMultiplier * LightFunction.QUADRATIC.apply(dist, maxLight);
                if (!DynamicLightsStorage.setLightLevel(mutable, lightLevel, false)) continue;
            }
            // do not check blocks that has been scheduled;
            DynamicLightsStorage.BP_UPDATED.putIfAbsent(mutable.asLong(), true);
        }
    }
}
