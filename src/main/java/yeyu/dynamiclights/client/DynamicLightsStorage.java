package yeyu.dynamiclights.client;

import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yeyu.dynamiclights.client.animation.EaseOutCubic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicLightsStorage {
    public static final Map<Item, Integer> ITEM_BLOCK_LIGHT_LEVEL = new HashMap<>();
    public static final Map<Long, Double> BP_TO_LIGHT_LEVEL = new ConcurrentHashMap<>();
    public static final Map<Long, Boolean> BP_UPDATED = new ConcurrentHashMap<>();
    public static final Map<Integer, EaseOutCubic> LIGHT_ANIMATE_INSTANCE = new HashMap<>();
    public static final Map<BlockPos, Integer> UNLIT_SCHEDULE = new HashMap<>();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean setLightLevel(BlockPos bp, double lightLevel, boolean force) {
        final long bpLong = bp.asLong();
        if (force) {
            final Double previous = BP_TO_LIGHT_LEVEL.put(bpLong, lightLevel);
            if (lightLevel < 1e-5) {
                return BP_TO_LIGHT_LEVEL.remove(bpLong) != null;
            }
            return previous != null && !MathHelper.approximatelyEquals(previous, lightLevel);
        }
        final Double current = BP_TO_LIGHT_LEVEL.getOrDefault(bpLong, .0);
        if (current > lightLevel) return false;
        BP_TO_LIGHT_LEVEL.put(bpLong, lightLevel);
        return true;
    }

    public static double getLightLevel(BlockPos bp) {
        return BP_TO_LIGHT_LEVEL.getOrDefault(bp.asLong(), .0);
    }

    public static float animationFactor(Entity entity, float newLight) {
        return animationFactor(entity.getId(), newLight);
    }

    public static float animationFactor(int entityId, float newLight) {
        final EaseOutCubic easeOutCubic = LIGHT_ANIMATE_INSTANCE.computeIfAbsent(entityId, $ -> new EaseOutCubic(0, newLight));
        return easeOutCubic.refreshAnimation(newLight);
    }

    public static void resetAnimation(Entity entity) {
        final int entityId = entity.getId();
        final EaseOutCubic easeOutCubic = LIGHT_ANIMATE_INSTANCE.computeIfAbsent(entityId, $ -> new EaseOutCubic(0, 0));
        easeOutCubic.overwriteFrom(0);
    }

    public static void flush() {
        BP_UPDATED.clear();
        BP_TO_LIGHT_LEVEL.clear();
    }

    public static void registerItemLightLevel() {
        registerItemLightLevel(Items.CAMPFIRE, Blocks.CAMPFIRE.getDefaultState().getLuminance() - 1, 5, 12);
        registerItemLightLevel(Items.SOUL_CAMPFIRE, Blocks.SOUL_CAMPFIRE.getDefaultState().getLuminance() - 1, 5, 12);
        registerItemLightLevel(Items.END_ROD, Blocks.END_ROD.getDefaultState().getLuminance() - 1, 5, 12);
        registerItemLightLevel(Items.GLOWSTONE, Blocks.GLOWSTONE.getDefaultState().getLuminance() - 1, 5, 12);
        registerItemLightLevel(Items.JACK_O_LANTERN, Blocks.JACK_O_LANTERN.getDefaultState().getLuminance() - 1, 5, 12);
        registerItemLightLevel(Items.LANTERN, Blocks.LANTERN.getDefaultState().getLuminance() - 1, 5, 12);
        registerItemLightLevel(Items.LAVA_BUCKET, Blocks.LAVA.getDefaultState().getLuminance() - 1, 5, 12);
        registerItemLightLevel(Items.TORCH, Blocks.TORCH.getDefaultState().getLuminance() - 1, 5, 12);
        registerItemLightLevel(Items.REDSTONE_TORCH, Blocks.REDSTONE_TORCH.getDefaultState().getLuminance() - 1, 5, 12);
        registerItemLightLevel(Items.SOUL_TORCH, Blocks.SOUL_TORCH.getDefaultState().getLuminance() - 1, 5, 12);
    }

    public static void registerItemLightLevel(Item matchedItem, int level, Integer lightEnchantmentInt, Integer lightFireInt) {
        level = Math.max(0, Math.min(15, level));
        int finalLevel = level;
        ITEM_BLOCK_LIGHT_LEVEL.put(matchedItem, finalLevel);

        DynamicLightsManager.INSTANCE.appendEntityTick(EntityType.ITEM, (entity, clientWorld) -> {
            final Item item = entity.getStack().getItem();
            if (item != matchedItem) return;
            DynamicLightsUtils.handleEntity(entity, clientWorld, finalLevel, lightEnchantmentInt, lightFireInt, true);
        });
    }

    public static int getItemLightLevel(Item item) {
        return ITEM_BLOCK_LIGHT_LEVEL.getOrDefault(item, 0);
    }

    public static void scheduleUnlit(BlockPos bp, int entityId) {
        final EaseOutCubic easeOutCubic = LIGHT_ANIMATE_INSTANCE.getOrDefault(entityId, null);
        if (easeOutCubic == null) return;
        UNLIT_SCHEDULE.put(bp.toImmutable(), entityId);
    }

    public static void tickUnlit(ClientWorld world) {
        for (Map.Entry<BlockPos, Integer> entry : UNLIT_SCHEDULE.entrySet()) {
            BlockPos blockPos = tickUnlit(entry, world);
            if (blockPos != null) {
                UNLIT_SCHEDULE.remove(blockPos);
            }
        }
    }

    public static BlockPos tickUnlit(Map.Entry<BlockPos, Integer> entry, ClientWorld world) {
        final BlockPos bp = entry.getKey();
        final Integer entityId = entry.getValue();
        final boolean shouldKeep = DynamicLightsUtils.handleEntityUnlit(bp, entityId, world);
        return shouldKeep ? null : bp;
    }
}
