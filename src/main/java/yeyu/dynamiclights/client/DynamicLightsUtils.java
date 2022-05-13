package yeyu.dynamiclights.client;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yeyu.dynamiclights.client.animation.EaseOutCubic;

public class DynamicLightsUtils {
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();

    private static int getHoldingItemLightLevel(LivingEntity entity) {
        final ItemStack mainHandItem = entity.getMainHandStack();
        final ItemStack offHandItem = entity.getOffHandStack();
        final int mainHandItemLightLevel = DynamicLightsStorage.getItemLightLevel(mainHandItem);
        final int offHandItemLightLevel = DynamicLightsStorage.getItemLightLevel(offHandItem);
        return Math.max(mainHandItemLightLevel, offHandItemLightLevel);
    }

    private static boolean hasEnchantedItems(LivingEntity entity) {
        for (ItemStack stack : entity.getArmorItems()) if (stack.hasEnchantments()) return true;
        return entity.getMainHandStack().hasEnchantments() || entity.getOffHandStack().hasEnchantments();
    }

    public static double getEntityHeldItemLightLevel(LivingEntity entity) {
        int itemLightLevel = entity.isSpectator() ? 8 : getHoldingItemLightLevel(entity);
        final DynamicLightsAttributes dynamicLightsAttributes = DynamicLightsAttributes.ENTITY2ATTRIBUTE.getOrDefault(entity.getType(), DynamicLightsAttributes.ENTITY_DEFAULT);
        itemLightLevel = Math.max(itemLightLevel, dynamicLightsAttributes.strength());
        itemLightLevel = Math.max(itemLightLevel, hasEnchantedItems(entity) ? dynamicLightsAttributes.enchantment() : 0);
        itemLightLevel = Math.max(itemLightLevel, entity.isOnFire() ? dynamicLightsAttributes.fire() : 0);
        itemLightLevel = Math.max(itemLightLevel, entity instanceof CreeperEntity creeperEntity && creeperEntity.isIgnited() ? dynamicLightsAttributes.fire() : 0);
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

    public static double getTnTLightLevel(TntEntity entity) {
        final DynamicLightsAttributes dynamicLightsAttributes = DynamicLightsAttributes.ENTITY2ATTRIBUTE.getOrDefault(entity.getType(), DynamicLightsAttributes.ENTITY_DEFAULT);
        final int itemLightLevel = dynamicLightsAttributes.fire();
        if (itemLightLevel == 0) {
            final EaseOutCubic easeOutCubic = DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.computeIfAbsent(entity.getId(), $ -> new EaseOutCubic(0, itemLightLevel));
            final double dimmingLightLevel = easeOutCubic.refreshAnimation(itemLightLevel);
            if (MathHelper.approximatelyEquals(dimmingLightLevel, 0)) {
                DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.remove(entity.getId());
                return 0;
            }
            return dimmingLightLevel;
        }
        final EaseOutCubic easeOutCubic = DynamicLightsStorage.ENTITY_TO_LIGHT_ANIMATE.computeIfAbsent(entity.getId(), $ -> new EaseOutCubic(0, itemLightLevel));
        return easeOutCubic.refreshAnimation(itemLightLevel);
    }

    public static double getItemEntityLightLevel(ItemEntity entity) {
        final DynamicLightsAttributes dynamicLightsAttributes = DynamicLightsAttributes.ITEM2ATTRIBUTE.getOrDefault(entity.getStack().getItem(), DynamicLightsAttributes.ITEM_DEFAULT);
        int itemLightLevel = dynamicLightsAttributes.strength();
        itemLightLevel = Math.max(itemLightLevel, entity.getStack().hasEnchantments() ? dynamicLightsAttributes.enchantment() : 0);
        itemLightLevel = Math.max(itemLightLevel, entity.isOnFire() ? dynamicLightsAttributes.fire() : 0);
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
