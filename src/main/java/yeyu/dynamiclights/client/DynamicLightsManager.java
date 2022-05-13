package yeyu.dynamiclights.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Triple;
import yeyu.dynamiclights.client.options.DynamicLightsOptions;
import yeyu.dynamiclights.client.options.DynamicLightsTickDelays;

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
        if (performance != DynamicLightsTickDelays.SMOOTH && (limiter = (Math.floorMod(++limiter, performance.SKIP_EVERY))) != 0)
            return;
        final MinecraftClient instance = MinecraftClient.getInstance();
        if (instance == null) return;
        final ClientPlayerEntity player = instance.player;
        if (player == null) return;

        world.getProfiler().push("tickBlockPostDynamicLights:gather_bps");
        final Vec3d pos = player.getPos();
        final Box box = Box.of(pos, 40, 40, 40);
        final List<Entity> nonSpectatingEntities = world.getNonSpectatingEntities(Entity.class, box);
        nonSpectatingEntities.sort((a, b) -> {
            final double ax = pos.squaredDistanceTo(a.getPos());
            final double bx = pos.squaredDistanceTo(b.getPos());
            return Double.compare(ax, bx);
        });

        int entitiesToTick = Math.min(nonSpectatingEntities.size(), DynamicLightsOptions.getMaxEntitiesToTick());
        for (int i = 0; entitiesToTick == 0 || i < nonSpectatingEntities.size(); i++) {

            Entity nonSpectatingEntity = nonSpectatingEntities.get(i);
            if (nonSpectatingEntity instanceof ItemEntity entity) {
                if (!entity.isOnGround()) continue;
                final BlockPos blockPos = entity.getBlockPos();
                final Vec3d entityPos = entity.getPos();
                final double entityLightLevel = DynamicLightsUtils.getItemEntityLightLevel(entity);
                if (MathHelper.approximatelyEquals(entityLightLevel, 0)) continue;

                final DynamicLightsObject dynamicLightsObject = DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.computeIfAbsent(blockPos.asLong(), $ -> new DynamicLightsObject(0));
                dynamicLightsObject.keepLit(entityLightLevel);
                DynamicLightsStorage.BP_TO_ORIGIN.put(blockPos.asLong(), Triple.of(
                        entityPos.getX(),
                        entityPos.getY(),
                        entityPos.getZ()
                ));
            } else if (nonSpectatingEntity instanceof LivingEntity entity) {
                final BlockPos blockPos = entity.getBlockPos();
                final Vec3d entityPos = entity.getPos();
                final double entityHeldItemLightLevel = DynamicLightsUtils.getEntityHeldItemLightLevel(entity);
                if (MathHelper.approximatelyEquals(entityHeldItemLightLevel, 0)) continue;
                entitiesToTick += entitiesToTick;
                final DynamicLightsObject dynamicLightsObject = DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.computeIfAbsent(blockPos.asLong(), $ -> new DynamicLightsObject(0));
                dynamicLightsObject.keepLit(entityHeldItemLightLevel);
                if (entity instanceof ClientPlayerEntity) {
                    final Vec3d camera = entity.getCameraPosVec(1);
                    Vec3d rotationVec = entity.getRotationVec(1);
                    Vec3d cameraPosVec = camera.add(rotationVec.x * 1.1, rotationVec.y * .3, rotationVec.z * 1.1);
                    DynamicLightsStorage.BP_TO_ORIGIN.put(blockPos.asLong(), Triple.of(
                            cameraPosVec.getX(),
                            cameraPosVec.getY(),
                            cameraPosVec.getZ()
                    ));
                } else {
                    DynamicLightsStorage.BP_TO_ORIGIN.put(blockPos.asLong(), Triple.of(
                            entityPos.getX(),
                            entityPos.getY(),
                            entityPos.getZ()
                    ));
                }
            } else if (nonSpectatingEntity instanceof TntEntity entity) {
                final BlockPos blockPos = entity.getBlockPos();
                final Vec3d entityPos = entity.getPos();
                final double entityLightLevel = DynamicLightsUtils.getTnTLightLevel(entity);
                if (MathHelper.approximatelyEquals(entityLightLevel, 0)) continue;

                final DynamicLightsObject dynamicLightsObject = DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.computeIfAbsent(blockPos.asLong(), $ -> new DynamicLightsObject(0));
                dynamicLightsObject.keepLit(entityLightLevel);
                DynamicLightsStorage.BP_TO_ORIGIN.put(blockPos.asLong(), Triple.of(
                        entityPos.getX(),
                        entityPos.getY(),
                        entityPos.getZ()
                ));
            }
        }

        world.getProfiler().swap("tickBlockPostDynamicLights:calculate_dynamic_lights");

        final HashSet<Long> bpChangedRecently = reusableHS.get();
        bpChangedRecently.clear();
        for (Map.Entry<Long, DynamicLightsObject> longDynamicLightsObjectEntry : DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.entrySet()) {
            final Long bpLong = longDynamicLightsObjectEntry.getKey();
            final DynamicLightsObject dynamicLightsObject = longDynamicLightsObjectEntry.getValue();
            final Triple<Double, Double, Double> origin = DynamicLightsStorage.BP_TO_ORIGIN.getOrDefault(bpLong, DynamicLightsStorage.ZERO_OFFSET);
            if (dynamicLightsObject.shouldKeepLit()) {
                final double lightLevel = dynamicLightsObject.value();
                DynamicLightsOptions.getSpreadness().computeDynamicLights(bpLong, origin.getLeft(), origin.getMiddle(), origin.getRight(), lightLevel, bpChangedRecently::contains, bpChangedRecently::add);
                dynamicLightsObject.ack();
            } else {
                DynamicLightsOptions.getSpreadness().computeLightsOff(bpLong, bpChangedRecently::contains, bpChangedRecently::add);
                DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.remove(bpLong);
                DynamicLightsStorage.BP_TO_ORIGIN.remove(bpLong);
            }
        }

        world.getProfiler().swap("tickBlockPostDynamicLights:check_block");

        final BlockPos.Mutable mutable = reusableBP.get();
        for (Long bpLong : reusableHS.get()) {
            mutable.set(
                    BlockPos.unpackLongX(bpLong),
                    BlockPos.unpackLongY(bpLong),
                    BlockPos.unpackLongZ(bpLong)
            );
            world.getChunkManager().getLightingProvider().checkBlock(mutable);
        }

        world.getProfiler().pop();

    }

    public void clear() {
        DynamicLightsStorage.BP_TO_LIGHT_LEVEL.clear();
        DynamicLightsStorage.BP_TO_ORIGIN.clear();
        DynamicLightsStorage.BP_TO_DYNAMIC_LIGHT_OBJ.clear();
        DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.clear();
        DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.clear();
    }

    public void resetLights() {
        final MinecraftClient instance = MinecraftClient.getInstance();
        if (instance == null) return;
        final ClientWorld world = instance.world;
        if (world == null) return;
        for (Map.Entry<Long, Double> longDoubleEntry : DynamicLightsStorage.BP_TO_LIGHT_LEVEL.entrySet()) {
            longDoubleEntry.setValue(0d);
        }

        final BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Map.Entry<Long, Double> longDoubleEntry : DynamicLightsStorage.BP_TO_LIGHT_LEVEL.entrySet()) {
            final Long bpLong = longDoubleEntry.getKey();
            mutable.set(
                    BlockPos.unpackLongX(bpLong),
                    BlockPos.unpackLongY(bpLong),
                    BlockPos.unpackLongZ(bpLong)
            );
            world.getChunkManager().getLightingProvider().checkBlock(mutable);
        }
        clear();
    }
}
