package yeyu.dynamiclights.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yeyu.dynamiclights.client.options.DynamicLightsOptions;

public class DynamicLightsUtils {
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean handleEntityUnlit(BlockPos bp, int entityId, ClientWorld clientWorld) {
        final Vec3d cameraPosVec = new Vec3d(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5);
        float maxLight = DynamicLightsStorage.animationFactor(entityId, 0);
        LOGGER.info("Tick unlit: id={} maxLight={}", entityId, maxLight);
        if (maxLight > 0) {
            DynamicLightsOptions.getCurrentOption().iterateLightMap(cameraPosVec, clientWorld, maxLight);
            return true;
        }
        return false;

    }

    public static void handleEntityUnlit(Entity entity, ClientWorld clientWorld, boolean animate) {
        handleEntity(entity, clientWorld, 0, 0, 0, animate);
    }

    public static void handleEntity(Entity entity, ClientWorld clientWorld, int fixedLightLevel, Integer lightEnchantmentInt, Integer lightFireInt, boolean animate) {
        Vec3d cameraPosVec = entity.getPos().add(0, 1, 0);
        if (!animate) {
            DynamicLightsStorage.resetAnimation(entity);
            DynamicLightsOptions.getCurrentOption().iterateLightMap(cameraPosVec, clientWorld, 0, true);
            return;
        }
        fixedLightLevel = Math.max(fixedLightLevel, hasEnchantment(entity) ? lightEnchantmentInt : 0);
        fixedLightLevel = Math.max(fixedLightLevel, entity.isOnFire() ? lightFireInt : 0);
        float maxLight = DynamicLightsStorage.animationFactor(entity, (entity instanceof LivingEntity livingEntity) && livingEntity.isDead() ? 0 : fixedLightLevel);
        if (maxLight > 0) DynamicLightsOptions.getCurrentOption().iterateLightMap(cameraPosVec, clientWorld, maxLight);
    }

    public static void handleEntityLightsByItem(Entity entity, ClientWorld clientWorld, float offset, Integer lightEnchantmentInt, Integer lightFireInt) {
        final Vec3d camera = entity.getCameraPosVec(1);
        Vec3d rotationVec = entity.getRotationVec(1);
        Vec3d cameraPosVec = camera.add(rotationVec.x * offset, rotationVec.y * .3, rotationVec.z * offset);
        int itemLightLevel = entity.isSpectator() ? 8 : getHoldingItemLightLevel(entity);
        itemLightLevel = Math.max(itemLightLevel, hasEnchantment(entity) ? lightEnchantmentInt : 0);
        itemLightLevel = Math.max(itemLightLevel, entity.isOnFire() ? lightFireInt : 0);
        float maxLight = DynamicLightsStorage.animationFactor(entity, itemLightLevel);
        if (maxLight > 0) DynamicLightsOptions.getCurrentOption().iterateLightMap(cameraPosVec, clientWorld, maxLight);
    }

    public static void handleEntityLightsOnPickup(Entity entity, ItemEntity itemPicked, ClientWorld clientWorld) {
        final Vec3d cameraPosVec = entity.getCameraPosVec(1);
        int itemLightLevel = itemPicked.isOnFire() ? 12 : DynamicLightsStorage.getItemLightLevel(itemPicked.getStack().getItem());
        float maxLight = DynamicLightsStorage.animationFactor(entity, itemLightLevel);
        if (maxLight > 0)
            DynamicLightsOptions.getCurrentOption().iterateLightMap(cameraPosVec, clientWorld, maxLight, true);
    }

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
}
