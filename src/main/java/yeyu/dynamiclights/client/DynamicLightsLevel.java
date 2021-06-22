package yeyu.dynamiclights.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public enum DynamicLightsLevel {
    OFF(5, 0, 0, 1),
    ONE(4, 0.135f, 2.57f, 0),
    TWO(5, 0.44f, 1.43f, .1f),
    THREE(6, 0.6f, 1.11f, .3f),
    FOUR(7, 0.545f, 1, .2f),
    FIVE(8, 0.545f, .9f, .4f),
    SIX(9, 0.545f, .84f, .6f);

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
        BlockPos.iterate(playerBp.add(-radius - 1, -radius - 1, -radius - 1), playerBp.add(radius, radius, radius)).forEach(bp -> processBlockPos(bp, cameraPosVec, clientWorld, maxLight, force));
        clientWorld.getProfiler().pop();
    }

    private void processBlockPos(BlockPos bp, Vec3d cameraPosVec, ClientWorld clientWorld, float maxLight, boolean forceOff) {
        if (this == DynamicLightsLevel.OFF || forceOff) {
            if (!DynamicLightsStorage.setLightLevel(bp, 0, true)) return;
        } else {
            final float x = bp.getX() + 0.5f;
            final float y = bp.getY() + 0.5f;
            final float z = bp.getZ() + 0.5f;
            final float camX = (float) cameraPosVec.getX();
            final float camY = (float) cameraPosVec.getY();
            final float camZ = (float) cameraPosVec.getZ();
            final float dx = camX - x;
            final float dy = camY - y;
            final float dz = camZ - z;
            final float dist = dx * dx + dy * dy + dz * dz;
            final double lightLevel = LightFunction.QUADRATIC.apply(dist, maxLight);
            if (!DynamicLightsStorage.setLightLevel(bp, lightLevel, false)) return;
        }
        // do not check blocks that has been scheduled;
        DynamicLightsStorage.BP_UPDATED.computeIfAbsent(bp.asLong(), $ -> {
            clientWorld.getChunkManager().getLightingProvider().checkBlock(bp);
            return true;
        });
    }
}
