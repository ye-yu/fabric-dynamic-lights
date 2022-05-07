package yeyu.dynamiclights.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class DynamicLightsClient implements ClientModInitializer {
    public static Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initialized dynamic lighting");
        DynamicLightsConfig.bootstrap();
        DynamicLightsStorage.registerItemLightLevel();
        ClientChunkEvents.CHUNK_LOAD.register(((world, chunk) -> {
            final int x = chunk.getPos().x;
            final int z = chunk.getPos().z;
            for (int y = world.getBottomSectionCoord(); y < world.getTopSectionCoord(); y++) {
                world.scheduleBlockRenders(x, y, z);
            }
        }));

        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            final ChunkPos pos = chunk.getPos();
            final int startX = pos.getStartX();
            final int startZ = pos.getStartZ();

            for (int dx = 0; dx < 16; dx++) {
                for (int dy = world.getBottomSectionCoord(); dy < world.getTopSectionCoord(); dy++) {
                    for (int dz = 0; dz < 16; dz++) {
                        final long bpLong = BlockPos.asLong(startX + dx, dy, startZ + dz);
                        DynamicLightsManager.INSTANCE.clear(bpLong);
                    }
                }
            }
        });

        ClientTickEvents.START_WORLD_TICK.register(DynamicLightsManager.INSTANCE::tickBlockPostDynamicLights);

    }
}
