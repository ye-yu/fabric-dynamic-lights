package yeyu.dynamiclights.client;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Triple;
import yeyu.dynamiclights.client.animation.EaseOutCubic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicLightsStorage {
    public static final Map<Item, Integer> ITEM_BLOCK_LIGHT_LEVEL = new HashMap<>();

    public static final Map<Long, DynamicLightsObject> BP_TO_DYNAMIC_LIGHT_OBJ = new ConcurrentHashMap<>();
    public static final Map<Long, Double> BP_TO_LIGHT_LEVEL = new ConcurrentHashMap<>();
    public static final Map<Long, Triple<Double, Double, Double>> BP_TO_ORIGIN = new ConcurrentHashMap<>();
    public static final Triple<Double, Double, Double> ZERO_OFFSET = Triple.of(0d,0d,0d);
    public static final Map<Integer, EaseOutCubic> ENTITY_TO_LIGHT_ANIMATE = new ConcurrentHashMap<>();

    public static double getLightLevel(BlockPos bp) {
        return BP_TO_LIGHT_LEVEL.getOrDefault(bp.asLong(), .0);
    }

    public static void registerItemLightLevel() {
        registerItemLightLevel(Items.CAMPFIRE, Blocks.CAMPFIRE.getDefaultState().getLuminance() - 1);
        registerItemLightLevel(Items.SOUL_CAMPFIRE, Blocks.SOUL_CAMPFIRE.getDefaultState().getLuminance() - 1);
        registerItemLightLevel(Items.END_ROD, Blocks.END_ROD.getDefaultState().getLuminance() - 1);
        registerItemLightLevel(Items.GLOWSTONE, Blocks.GLOWSTONE.getDefaultState().getLuminance() - 1);
        registerItemLightLevel(Items.JACK_O_LANTERN, Blocks.JACK_O_LANTERN.getDefaultState().getLuminance() - 1);
        registerItemLightLevel(Items.LANTERN, Blocks.LANTERN.getDefaultState().getLuminance() - 1);
        registerItemLightLevel(Items.LAVA_BUCKET, Blocks.LAVA.getDefaultState().getLuminance() - 1);
        registerItemLightLevel(Items.TORCH, Blocks.TORCH.getDefaultState().getLuminance() - 1);
        registerItemLightLevel(Items.REDSTONE_TORCH, Blocks.REDSTONE_TORCH.getDefaultState().getLuminance() - 1);
        registerItemLightLevel(Items.SOUL_TORCH, Blocks.SOUL_TORCH.getDefaultState().getLuminance() - 1);
    }

    public static void registerItemLightLevel(Item matchedItem, int level) {
        level = Math.max(0, Math.min(15, level));
        int finalLevel = level;
        ITEM_BLOCK_LIGHT_LEVEL.put(matchedItem, finalLevel);
    }

    public static int getItemLightLevel(Item item) {
        return ITEM_BLOCK_LIGHT_LEVEL.getOrDefault(item, 0);
    }
}
