package yeyu.dynamiclights.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public enum DynamicLightManager {
    INSTANCE;
    private static final BiConsumer<? super Entity, ClientWorld> NO_OP_TICK = (BiConsumer<Entity, ClientWorld>) (entity, clientWorld) -> {
    };
    private final Map<Identifier, BiConsumer<? super Entity, ClientWorld>> tickMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Entity> void registerEntityTick(EntityType<T> entityType, BiConsumer<? super T, ClientWorld> tickConsumer) {
        tickMap.put(Registry.ENTITY_TYPE.getId(entityType), (BiConsumer<? super Entity, ClientWorld>) tickConsumer);
    }

    public void tick(Entity entity, ClientWorld world) {
        final EntityType<?> type = entity.getType();
        final Identifier id = Registry.ENTITY_TYPE.getId(type);
        tickMap.getOrDefault(id, NO_OP_TICK).accept(entity, world);
    }

    public void tickEntities(ClientWorld clientWorld) {
        final MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.player == null) return;
        final Vec3d camera = minecraftClient.player.getCameraPosVec(1);
        final double maxDistance = minecraftClient.options.viewDistance * 16;
        final double maxDistanceSqrd = maxDistance * maxDistance;
        clientWorld.getEntities().forEach(entity -> {
            if (entity.getPos().distanceTo(camera) > maxDistanceSqrd) return;
            tick(entity, clientWorld);
        });
    }
}
