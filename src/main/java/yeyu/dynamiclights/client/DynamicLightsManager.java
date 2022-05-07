package yeyu.dynamiclights.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yeyu.dynamiclights.client.options.DynamicLightsOptions;
import yeyu.dynamiclights.client.options.DynamicLightsTickDelays;

import java.util.List;
import java.util.Map;

public enum DynamicLightsManager {
    INSTANCE;

    private static int limiter = 0;

    public final ThreadLocal<BlockPos.Mutable> reusableBP = new ThreadLocal<>() {{
        set(new BlockPos.Mutable());
    }};

    public void tickBlockPostDynamicLights(ClientWorld world) {
        final DynamicLightsTickDelays performance = DynamicLightsOptions.getPerformance();
        if (performance != DynamicLightsTickDelays.SMOOTH && (limiter = (Math.floorMod(++limiter, performance.SKIP_EVERY))) != 0) return;
        final MinecraftClient instance = MinecraftClient.getInstance();
        if (instance == null) return;
        final ClientPlayerEntity player = instance.player;
        if (player == null) return;

        final Vec3d pos = player.getPos();
        final Box box = Box.of(pos, 40, 40, 40);
        final List<LivingEntity> nonSpectatingEntities = world.getNonSpectatingEntities(LivingEntity.class, box);
        nonSpectatingEntities.sort((a, b) -> {
            final double ax = pos.squaredDistanceTo(a.getPos());
            final double bx = pos.squaredDistanceTo(b.getPos());
            return Double.compare(ax, bx);
        });

        final int entitiesToTick = Math.min(nonSpectatingEntities.size(), DynamicLightsOptions.getMaxEntitiesToTick());
        for (int i = 0; i < entitiesToTick; i++) {
            LivingEntity entity = nonSpectatingEntities.get(i);
            final BlockPos blockPos = entity.getBlockPos();
            final double entityHeldItemLightLevel = DynamicLightsUtils.getEntityHeldItemLightLevel(entity, 7, 12);
            final DynamicLightsObject dynamicLightsObject = DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.computeIfAbsent(blockPos.asLong(), $ -> new DynamicLightsObject(0));
            dynamicLightsObject.shift(entityHeldItemLightLevel);
        }

        // for item entity, only tick dynamic lights when it is on the ground
        final List<ItemEntity> nonSpectatingItemEntities = world.getNonSpectatingEntities(ItemEntity.class, box);
        for (ItemEntity entity : nonSpectatingItemEntities) {
            if (!entity.isOnGround()) continue;
            final BlockPos blockPos = entity.getBlockPos();
            final double entityLightLevel = DynamicLightsUtils.getItemEntityLightLevel(entity, 7, 12);
            final DynamicLightsObject dynamicLightsObject = DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.computeIfAbsent(blockPos.asLong(), $ -> new DynamicLightsObject(0));
            dynamicLightsObject.shift(entityLightLevel);
        }

        DynamicLightsStorage.BP_TO_LIGHT_LEVEL.clear();
        for (Map.Entry<Long, DynamicLightsObject> longDynamicLightsObjectEntry : DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.entrySet()) {
            final Long bpLong = longDynamicLightsObjectEntry.getKey();
            final DynamicLightsObject dynamicLightsObject = longDynamicLightsObjectEntry.getValue();
            if (dynamicLightsObject.isDirty()) {
                final double lightLevel = dynamicLightsObject.value();
                dynamicLightsObject.ack();
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -1; dy < 2; dy++) {
                        for (int dz = -1; dz < 2; dz++) {
                            final double dist = Math.hypot(dx, Math.hypot(dy, dz));
                            final double lightFactor = 1 - dist * 0.33333333;
                            final double newLight = MathHelper.clamp(lightFactor * lightLevel, 0, 15);
                            DynamicLightsStorage.BP_TO_LIGHT_LEVEL.merge(bpLong, newLight, Math::max);
                        }
                    }
                }
            } else {
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -1; dy < 2; dy++) {
                        for (int dz = -1; dz < 2; dz++) {
                            DynamicLightsStorage.BP_TO_LIGHT_LEVEL.merge(bpLong, 0d, Math::max);
                        }
                    }
                }
                DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.remove(bpLong);
            }
        }

        final BlockPos.Mutable mutable = reusableBP.get();
        for (Long bpLong : DynamicLightsStorage.BP_TO_LIGHT_LEVEL.keySet()) {
            mutable.set(
                    BlockPos.unpackLongX(bpLong),
                    BlockPos.unpackLongY(bpLong),
                    BlockPos.unpackLongZ(bpLong)
            );
            world.getLightingProvider().checkBlock(mutable);
        }
    }

    public void clear() {

    }
}
