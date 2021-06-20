package yeyu.dynamiclights.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
    }
}
