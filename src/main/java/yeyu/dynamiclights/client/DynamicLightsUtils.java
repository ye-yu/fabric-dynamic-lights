package yeyu.dynamiclights.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class DynamicLightsUtils {

    public static void handleEntityNoLight(Entity entity, ClientWorld clientWorld) {
        handleEntity(entity, clientWorld, 0, 0, 0);
    }
    public static void handleEntity(Entity entity, ClientWorld clientWorld, int fixedLightLevel, Integer lightEnchantmentInt, Integer lightFireInt) {
        Vec3d cameraPosVec = entity.getCameraPosVec(1);
        fixedLightLevel = Math.max(fixedLightLevel, hasEnchantment(entity) ? lightEnchantmentInt : 0);
        fixedLightLevel = Math.max(fixedLightLevel, entity.isOnFire() ? lightFireInt : 0);
        float maxLight = DynamicLightsStorage.animationFactor(entity, (entity instanceof LivingEntity) &&  ((LivingEntity)entity).isDead() ? 0 : fixedLightLevel);
        if (maxLight > 0) DynamicLightsOption.getCurrentOption().iterateLightMap(cameraPosVec, clientWorld, maxLight);
    }

    public static void handleEntityLightsByItem(Entity entity, ClientWorld clientWorld, float offset, Integer lightEnchantmentInt, Integer lightFireInt) {
        final Vec3d camera = entity.getCameraPosVec(1);
        Vec3d rotationVec = entity.getRotationVec(1);
        Vec3d cameraPosVec = camera.add(rotationVec.x * offset, rotationVec.y * offset, rotationVec.z * offset);
        int itemLightLevel = entity.isSpectator() ? 8 : getItemLightLevel(entity);
        itemLightLevel = Math.max(itemLightLevel, hasEnchantment(entity) ? lightEnchantmentInt : 0);
        itemLightLevel = Math.max(itemLightLevel, entity.isOnFire() ? lightFireInt : 0);
        float maxLight = DynamicLightsStorage.animationFactor(entity, itemLightLevel);
        if (maxLight > 0) DynamicLightsOption.getCurrentOption().iterateLightMap(cameraPosVec, clientWorld, maxLight);
    }

    private static int getItemLightLevel(Entity entity) {
        if (!(entity instanceof LivingEntity)) return 0;
        final Item mainHandItem = ((LivingEntity) entity).getMainHandStack().getItem();
        final Item offHandItem = ((LivingEntity) entity).getOffHandStack().getItem();
        final Integer mainHandItemLightLevel = DynamicLightsClient.ITEM_BLOCK_LIGHT_LEVEL.getOrDefault(mainHandItem, 0);
        final Integer offHandItemLightLevel = DynamicLightsClient.ITEM_BLOCK_LIGHT_LEVEL.getOrDefault(offHandItem, 0);
        return Math.max(mainHandItemLightLevel, offHandItemLightLevel);
    }

    private static boolean hasEnchantment(Entity entity) {
        if (entity instanceof InventoryOwner) {
            final Inventory inventory = ((InventoryOwner) entity).getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                final ItemStack stack = inventory.getStack(i);
                if (stack.hasEnchantments()) return true;
            }
            return false;
        }
        if (entity instanceof PlayerEntity) {
            final Inventory inventory = ((PlayerEntity) entity).getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                final ItemStack stack = inventory.getStack(i);
                if (stack.hasEnchantments()) return true;
            }
            return false;
        }
        if (entity instanceof ItemEntity) {
            return ((ItemEntity) entity).getStack().hasEnchantments();
        }
        return false;
    }
}
