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
import org.apache.commons.lang3.tuple.Triple;
import yeyu.dynamiclights.client.options.DynamicLightsOptions;
import yeyu.dynamiclights.client.options.DynamicLightsTickDelays;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public enum DynamicLightsManager {
    INSTANCE;

    private static int limiter = 0;

    public final ThreadLocal<BlockPos.Mutable> reusableBP = new ThreadLocal<>() {{
        set(new BlockPos.Mutable());
    }};

    public final ThreadLocal<HashSet<Long>> reusableHS = new ThreadLocal<>() {{
        set(new HashSet<>());
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

        int entitiesToTick = Math.min(nonSpectatingEntities.size(), DynamicLightsOptions.getMaxEntitiesToTick());
        for (int i = 0; entitiesToTick == 0 || i < nonSpectatingEntities.size(); i++) {
            LivingEntity entity = nonSpectatingEntities.get(i);
            final BlockPos blockPos = entity.getBlockPos();
            final Vec3d entityPos = entity.getPos();
            final double entityHeldItemLightLevel = DynamicLightsUtils.getEntityHeldItemLightLevel(entity, 7, 12);
            if (MathHelper.approximatelyEquals(entityHeldItemLightLevel, 0)) continue;
            entitiesToTick += entitiesToTick;
            final DynamicLightsObject dynamicLightsObject = DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.computeIfAbsent(blockPos.asLong(), $ -> new DynamicLightsObject(0));
            dynamicLightsObject.keepLit(entityHeldItemLightLevel);
            DynamicLightsStorage.BP_TO_ORIGIN.put(blockPos.asLong(), Triple.of(
                    entityPos.getX(),
                    entityPos.getY(),
                    entityPos.getZ()
            ));
        }

        // for item entity, only tick dynamic lights when it is on the ground
        final List<ItemEntity> nonSpectatingItemEntities = world.getNonSpectatingEntities(ItemEntity.class, box);
        for (ItemEntity entity : nonSpectatingItemEntities) {
            if (!entity.isOnGround()) continue;
            final BlockPos blockPos = entity.getBlockPos();
            final int entityLightLevel = DynamicLightsUtils.getItemEntityLightLevel(entity, 7, 12);
            final DynamicLightsObject dynamicLightsObject = DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.computeIfAbsent(blockPos.asLong(), $ -> new DynamicLightsObject(0));
            dynamicLightsObject.keepLit(entityLightLevel);
        }

        final ArrayList<Long> toRemove = new ArrayList<>(DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.size());
        final HashSet<Long> bpChangedRecently = reusableHS.get();
        bpChangedRecently.clear();
        for (Map.Entry<Long, DynamicLightsObject> longDynamicLightsObjectEntry : DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.entrySet()) {
            final Long bpLong = longDynamicLightsObjectEntry.getKey();
            final DynamicLightsObject dynamicLightsObject = longDynamicLightsObjectEntry.getValue();
            final Triple<Double, Double, Double> origin = DynamicLightsStorage.BP_TO_ORIGIN.getOrDefault(bpLong, DynamicLightsStorage.ZERO_OFFSET);
            if (dynamicLightsObject.shouldKeepLit()) {
                final double lightLevel = dynamicLightsObject.value();
                // if approximately the same, no need to schedule to check on the same block pos
                // only merge value with the block pos that other has updated
                DynamicLightsOptions.getSpreadness().computeDynamicLights(bpLong, origin.getLeft(), origin.getMiddle(), origin.getRight(), lightLevel, dynamicLightsObject.isApproximatelySame(), bpChangedRecently::add);
                dynamicLightsObject.ack();
            } else {
                DynamicLightsOptions.getSpreadness().computeLightsOff(bpLong, bpChangedRecently::contains);
                toRemove.add(bpLong);
            }
        }

        final BlockPos.Mutable mutable = reusableBP.get();
        for (Long bpLong : DynamicLightsStorage.BP_TO_LIGHT_LEVEL.keySet()) {
            mutable.set(
                    BlockPos.unpackLongX(bpLong),
                    BlockPos.unpackLongY(bpLong),
                    BlockPos.unpackLongZ(bpLong)
            );
            world.getChunkManager().getLightingProvider().checkBlock(mutable);
        }

        for (Long bpLong : toRemove) {
            DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.remove(bpLong);
            DynamicLightsStorage.BP_TO_LIGHT_LEVEL.remove(bpLong);
            DynamicLightsStorage.BP_TO_ORIGIN.remove(bpLong);
        }
    }

    public void clear() {
        DynamicLightsStorage.BP_TO_LIGHT_LEVEL.clear();
        DynamicLightsStorage.BP_TO_ORIGIN.clear();
        DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.clear();
        DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.clear();
    }

    public void clear(long bpLong) {
        DynamicLightsStorage.BP_TO_LIGHT_LEVEL.remove(bpLong);
        DynamicLightsStorage.BP_TO_ORIGIN.remove(bpLong);
        DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.remove(bpLong);
    }
}
