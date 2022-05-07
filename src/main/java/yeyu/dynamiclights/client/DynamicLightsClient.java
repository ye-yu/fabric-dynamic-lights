package yeyu.dynamiclights.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
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

        ClientTickEvents.START_WORLD_TICK.register(DynamicLightsManager.INSTANCE::tickBlockPostDynamicLights);
    }
}
