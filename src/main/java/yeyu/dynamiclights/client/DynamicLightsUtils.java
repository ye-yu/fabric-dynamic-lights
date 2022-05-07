package yeyu.dynamiclights.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        return itemLightLevel;
    }

    public static int getItemEntityLightLevel(ItemEntity entity, Integer lightEnchantmentInt, Integer lightFireInt) {
        int itemLightLevel = DynamicLightsStorage.ITEM_BLOCK_LIGHT_LEVEL.getOrDefault(entity.getStack().getItem(), 0);
        itemLightLevel = Math.max(itemLightLevel, entity.getStack().hasEnchantments() ? lightEnchantmentInt : 0);
        itemLightLevel = Math.max(itemLightLevel, entity.isOnFire() ? lightFireInt : 0);
        return itemLightLevel;
    }
}
