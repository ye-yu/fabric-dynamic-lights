package yeyu.dynamiclights.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yeyu.dynamiclights.client.animation.EaseOutCubic;

public class DynamicLightsUtils {
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();

    private static int getHoldingItemLightLevel(Entity entity) {
        if (!(entity instanceof LivingEntity)) return 0;
        final Item mainHandItem = ((LivingEntity) entity).getMainHandStack().getItem();
        final Item offHandItem = ((LivingEntity) entity).getOffHandStack().getItem();
        final int mainHandItemLightLevel = DynamicLightsStorage.getItemLightLevel(mainHandItem);
        final int offHandItemLightLevel = DynamicLightsStorage.getItemLightLevel(offHandItem);
        return Math.max(mainHandItemLightLevel, offHandItemLightLevel);
    }

    private static boolean hasEnchantment(Entity entity) {
        if (entity instanceof ItemEntity) return ((ItemEntity) entity).getStack().hasEnchantments();
        for (ItemStack stack : entity.getArmorItems()) if (stack.hasEnchantments()) return true;
        if (entity instanceof LivingEntity) {
            if (((LivingEntity) entity).getMainHandStack().hasEnchantments()) return true;
            return ((LivingEntity) entity).getOffHandStack().hasEnchantments();
        }
        return false;
    }

    public static double getEntityHeldItemLightLevel(Entity entity, Integer lightEnchantmentInt, Integer lightFireInt) {
        int itemLightLevel = entity.isSpectator() ? 8 : getHoldingItemLightLevel(entity);
        itemLightLevel = Math.max(itemLightLevel, hasEnchantment(entity) ? lightEnchantmentInt : 0);
        itemLightLevel = Math.max(itemLightLevel, entity.isOnFire() ? lightFireInt : 0);
        itemLightLevel = Math.max(itemLightLevel, entity instanceof TntEntity ? lightFireInt : 0);
        itemLightLevel = Math.max(itemLightLevel, entity instanceof CreeperEntity creeperEntity && creeperEntity.isIgnited() ? lightFireInt : 0);
        final int itemLightLevelFinal = itemLightLevel;
        if (itemLightLevel == 0) {
            final EaseOutCubic easeOutCubic = DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.computeIfAbsent(entity.getId(), $ -> new EaseOutCubic(0, itemLightLevelFinal));
            final double dimmingLightLevel = easeOutCubic.refreshAnimation(itemLightLevel);
            if (MathHelper.approximatelyEquals(dimmingLightLevel, 0)) {
                DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.remove(entity.getId());
                return 0;
            }
            return dimmingLightLevel;
        }
        final EaseOutCubic easeOutCubic = DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.computeIfAbsent(entity.getId(), $ -> new EaseOutCubic(0, itemLightLevelFinal));
        return easeOutCubic.refreshAnimation(itemLightLevel);
    }

    public static double getItemEntityLightLevel(ItemEntity entity, Integer lightEnchantmentInt, Integer lightFireInt) {
        int itemLightLevel = DynamicLightsStorage.ITEM_BLOCK_LIGHT_LEVEL.getOrDefault(entity.getStack().getItem(), 0);
        itemLightLevel = Math.max(itemLightLevel, entity.getStack().hasEnchantments() ? lightEnchantmentInt : 0);
        itemLightLevel = Math.max(itemLightLevel, entity.isOnFire() ? lightFireInt : 0);
        final int itemLightLevelFinal = itemLightLevel;
        if (itemLightLevel == 0) {
            final EaseOutCubic easeOutCubic = DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.computeIfAbsent(entity.getId(), $ -> new EaseOutCubic(0, itemLightLevelFinal));
            final double dimmingLightLevel = easeOutCubic.refreshAnimation(itemLightLevel);
            if (MathHelper.approximatelyEquals(dimmingLightLevel, 0)) {
                DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.remove(entity.getId());
                return 0;
            }
            return dimmingLightLevel;
        }
        final EaseOutCubic easeOutCubic = DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.computeIfAbsent(entity.getId(), $ -> new EaseOutCubic(0, itemLightLevelFinal));
        return easeOutCubic.refreshAnimation(itemLightLevel);
    }
}
