package yeyu.dynamiclights.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public enum DynamicLightsManager {
    INSTANCE;
    private static final BiConsumer<? super Entity, ClientWorld> NO_OP_TICK = (BiConsumer<Entity, ClientWorld>) (entity, clientWorld) -> {
    };

    private static int limiter = 0;

    private final Map<Identifier, BiConsumer<? super Entity, ClientWorld>> tickMap = new HashMap<>();

    public void clear() {
        tickMap.clear();
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> void registerEntityTick(Identifier entityType, BiConsumer<? super T, ClientWorld> tickConsumer) {
        tickMap.put(entityType, (BiConsumer<? super Entity, ClientWorld>) tickConsumer);
    }

    @SuppressWarnings({"unchecked", "unused"})
    public <T extends Entity> void registerEntityTick(EntityType<T> entityType, BiConsumer<? super T, ClientWorld> tickConsumer) {
        registerEntityTick(Registry.ENTITY_TYPE.getId(entityType), (BiConsumer<? super Entity, ClientWorld>) tickConsumer);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> void appendEntityTick(EntityType<T> entityType, BiConsumer<? super T, ClientWorld> tickConsumer) {
        appendEntityTick(Registry.ENTITY_TYPE.getId(entityType), (BiConsumer<? super Entity, ClientWorld>) tickConsumer);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> void appendEntityTick(Identifier key, BiConsumer<? super T, ClientWorld> tickConsumer) {
        if (tickMap.containsKey(key)) {
            final BiConsumer<? super Entity, ClientWorld> clientWorldBiConsumer = tickMap.get(key);
            registerEntityTick(key, ((entity, clientWorld) -> {
                clientWorldBiConsumer.accept(entity, clientWorld);
                ((BiConsumer<? super Entity, ClientWorld>) tickConsumer).accept(entity, clientWorld);
            }));
        } else tickMap.put(key, (BiConsumer<? super Entity, ClientWorld>) tickConsumer);
    }


    public void tick(Entity entity, ClientWorld world) {
        final EntityType<?> type = entity.getType();
        final Identifier id = Registry.ENTITY_TYPE.getId(type);
        tickMap.getOrDefault(id, NO_OP_TICK).accept(entity, world);
    }

    public void tickEntities(ClientWorld clientWorld) {
        final DynamicLightsTicks tickLevel = DynamicLightsOptions.getTickLevel();
        if (tickLevel != DynamicLightsTicks.SMOOTH && (limiter = ++limiter % tickLevel.SKIP_EVERY) > 0) return;
        DynamicLightsStorage.flush();
        final MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.cameraEntity == null) return;
        final Vec3d pos = minecraftClient.cameraEntity.getPos();
        final AtomicInteger count = new AtomicInteger(0);
        final double maxDistance = minecraftClient.options.viewDistance * 16;
        final Box box = Box.of(pos, maxDistance, maxDistance, maxDistance);
        final List<Entity> nonSpectatingEntities = clientWorld.getEntitiesByClass(Entity.class, box, entity -> DynamicLightsManager.INSTANCE.tickMap.containsKey(Registry.ENTITY_TYPE.getId(entity.getType())));
        nonSpectatingEntities.sort((a, b) -> {
            final double da = pos.distanceTo(a.getPos());
            final double db = pos.distanceTo(b.getPos());
            return Double.compare(da, db);
        });
        nonSpectatingEntities.forEach(entity -> tickEntity(entity, clientWorld, DynamicLightsOptions.getMaxEntitiesToTick(), count::incrementAndGet));
        DynamicLightsStorage.tickUnlit(clientWorld);
    }

    private void tickEntity(Entity entity, ClientWorld clientWorld, int maxIteration, Supplier<Integer> increment) {
        if (entity.getType() != EntityType.PLAYER && increment.get() > maxIteration) {
            clientWorld.getProfiler().push("dynamiclight-unlit-" + Registry.ENTITY_TYPE.getId(entity.getType()));
            DynamicLightsUtils.handleEntityUnlit(entity, clientWorld, true);
            clientWorld.getProfiler().pop();
            return;
        }
        final MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.player == null) return;
        final Vec3d camera = minecraftClient.player.getCameraPosVec(1);
        final double maxDistance = minecraftClient.options.viewDistance * 4;
        final double maxDistanceSqrd = maxDistance * maxDistance;
        if (entity.getPos().distanceTo(camera) > maxDistanceSqrd) return;
        clientWorld.getProfiler().push("dynamiclight-" + Registry.ENTITY_TYPE.getId(entity.getType()));
        tick(entity, clientWorld);
        clientWorld.getProfiler().pop();
    }
}
