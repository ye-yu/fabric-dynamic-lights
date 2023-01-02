package yeyu.dynamiclights.client;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Triple;
import yeyu.dynamiclights.client.animation.EaseOutCubic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicLightsStorage {
    public static final Map<Long, DynamicLightsObject> BP_TO_DYNAMIC_LIGHT_OBJ = new ConcurrentHashMap<>();
    public static final Map<Long, Double> BP_TO_LIGHT_LEVEL = new ConcurrentHashMap<>();
    public static final Map<Long, Triple<Double, Double, Double>> BP_TO_ORIGIN = new ConcurrentHashMap<>();
    public static final Triple<Double, Double, Double> ZERO_OFFSET = Triple.of(0d, 0d, 0d);
    public static final Map<Integer, EaseOutCubic> ENTITY_TO_LIGHT_ANIMATE = new ConcurrentHashMap<>();

    public static double getLightLevel(BlockPos bp) {
        return BP_TO_LIGHT_LEVEL.getOrDefault(bp.asLong(), .0);
    }

    public static void registerItemLightLevel() {
        DynamicLightsAttributes.registerItemLightLevel(Items.CAMPFIRE, Blocks.CAMPFIRE.getDefaultState().getLuminance() - 1);
        DynamicLightsAttributes.registerItemLightLevel(Items.SOUL_CAMPFIRE, Blocks.SOUL_CAMPFIRE.getDefaultState().getLuminance() - 1);
        DynamicLightsAttributes.registerItemLightLevel(Items.END_ROD, Blocks.END_ROD.getDefaultState().getLuminance() - 1);
        DynamicLightsAttributes.registerItemLightLevel(Items.GLOWSTONE, Blocks.GLOWSTONE.getDefaultState().getLuminance() - 1);
        DynamicLightsAttributes.registerItemLightLevel(Items.JACK_O_LANTERN, Blocks.JACK_O_LANTERN.getDefaultState().getLuminance() - 1);
        DynamicLightsAttributes.registerItemLightLevel(Items.LANTERN, Blocks.LANTERN.getDefaultState().getLuminance() - 1);
        DynamicLightsAttributes.registerItemLightLevel(Items.LAVA_BUCKET, Blocks.LAVA.getDefaultState().getLuminance() - 1);
        DynamicLightsAttributes.registerItemLightLevel(Items.TORCH, Blocks.TORCH.getDefaultState().getLuminance() - 1);
        DynamicLightsAttributes.registerItemLightLevel(Items.REDSTONE_TORCH, Blocks.REDSTONE_TORCH.getDefaultState().getLuminance() - 1);
        DynamicLightsAttributes.registerItemLightLevel(Items.SOUL_TORCH, Blocks.SOUL_TORCH.getDefaultState().getLuminance() - 1);
    }

    public static int getItemLightLevel(ItemStack item) {
        final DynamicLightsAttributes attributes = DynamicLightsAttributes.ITEM2ATTRIBUTE.getOrDefault(item.getItem(), DynamicLightsAttributes.ITEM_DEFAULT);
        final boolean b = item.hasEnchantments();
        return b ? attributes.enchantment() : attributes.strength();
    }
}
