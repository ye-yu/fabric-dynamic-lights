package yeyu.dynamiclights.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class DynamicLightsClient implements ClientModInitializer {
    public static Logger LOGGER = LogManager.getLogger();
    public static Map<Long, Double> BP_TO_LIGHT_LEVEL = new HashMap<>();
    public static Map<Item, Integer> ITEM_BLOCK_LIGHT_LEVEL = new HashMap<>();

    private static void handlePlayer(PlayerEntity entity, ClientWorld clientWorld) {
        final Vec3d camera = entity.getCameraPosVec(1);
        Vec3d rotationVec = entity.getRotationVec(1);
        Vec3d cameraPosVec = camera.add(rotationVec.x * 1.5, rotationVec.y * 1.5, rotationVec.z * 1.5);
        final int maxLight = entity.isSpectator() ? 0 : DynamicLightsClient.ITEM_BLOCK_LIGHT_LEVEL.getOrDefault(entity.getMainHandStack().getItem(), 0);
        DynamicLightsOption.getCurrentOption().iterateLightMap(cameraPosVec, clientWorld, maxLight);
    }

    public static boolean setLightLevel(BlockPos bp, double lightLevel) {
        final long bpLong = bp.asLong();
        if (lightLevel < 1e-5) {
            return BP_TO_LIGHT_LEVEL.remove(bpLong) != null;
        } else {
            final Double current = BP_TO_LIGHT_LEVEL.getOrDefault(bpLong, .0);
            if (current > lightLevel) return false;
            BP_TO_LIGHT_LEVEL.put(bpLong, lightLevel);
            return true;
        }
    }

    public static double getLightLevel(BlockPos bp) {
        return BP_TO_LIGHT_LEVEL.getOrDefault(bp.asLong(), .0);
    }

    /**
     * "minecraft:beacon",
     * "minecraft:campfire",
     * "minecraft:end_rod",
     * "minecraft:glowstone",
     * "minecraft:jack_o_lantern",
     * "minecraft:lantern",
     * "minecraft:lava_bucket",
     * "minecraft:torch"
     */
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initialized dynamic lighting");
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.BEACON, Blocks.BEACON.getDefaultState().getLuminance());
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.CAMPFIRE, Blocks.CAMPFIRE.getDefaultState().getLuminance());
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.SOUL_CAMPFIRE, Blocks.SOUL_CAMPFIRE.getDefaultState().getLuminance());
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.END_ROD, Blocks.END_ROD.getDefaultState().getLuminance());
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.GLOWSTONE, Blocks.GLOWSTONE.getDefaultState().getLuminance());
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.JACK_O_LANTERN, Blocks.JACK_O_LANTERN.getDefaultState().getLuminance());
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.LANTERN, Blocks.LANTERN.getDefaultState().getLuminance());
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.LAVA_BUCKET, Blocks.LAVA.getDefaultState().getLuminance());
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.TORCH, Blocks.TORCH.getDefaultState().getLuminance());
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.REDSTONE_TORCH, Blocks.REDSTONE_TORCH.getDefaultState().getLuminance());
        ITEM_BLOCK_LIGHT_LEVEL.put(Items.SOUL_TORCH, Blocks.SOUL_TORCH.getDefaultState().getLuminance());

        LOGGER.info("Registering default light handler");
        DynamicLightManager.INSTANCE.registerEntityTick(EntityType.PLAYER, DynamicLightsClient::handlePlayer);
    }
}
