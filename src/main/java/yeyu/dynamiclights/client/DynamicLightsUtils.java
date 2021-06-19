package yeyu.dynamiclights.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;

public class DynamicLightsUtils {
    public static void handleEntity(Entity entity, ClientWorld clientWorld, int fixedLightLevel) {
        final Vec3d camera = entity.getCameraPosVec(1);
        Vec3d rotationVec = entity.getRotationVec(1);
        Vec3d cameraPosVec = camera.add(rotationVec.x * 1.5, rotationVec.y * 1.5, rotationVec.z * 1.5);
        final float animationFactor = DynamicLightsStorage.animationFactor(entity, (entity instanceof LivingEntity) &&  ((LivingEntity)entity).isDead() ? 0 : fixedLightLevel);
        DynamicLightsOption.getCurrentOption().iterateLightMap(cameraPosVec, clientWorld, animationFactor);
    }

    public static void handleEntityLightsByItem(Entity entity, ClientWorld clientWorld, float offset) {
        final Vec3d camera = entity.getCameraPosVec(1);
        Vec3d rotationVec = entity.getRotationVec(1);
        Vec3d cameraPosVec = camera.add(rotationVec.x * offset, rotationVec.y * offset, rotationVec.z * offset);
        final int maxLight = entity.isSpectator() ? 8 : getItemLightLevel(entity);
        final float animationFactor = DynamicLightsStorage.animationFactor(entity, maxLight);
        DynamicLightsOption.getCurrentOption().iterateLightMap(cameraPosVec, clientWorld, animationFactor);
    }

    private static int getItemLightLevel(Entity entity) {
        if ((entity instanceof LivingEntity)) {
            final Item mainHandItem = ((LivingEntity) entity).getMainHandStack().getItem();
            final Item offHandItem = ((LivingEntity) entity).getOffHandStack().getItem();
            final Integer mainHandItemLightLevel = DynamicLightsClient.ITEM_BLOCK_LIGHT_LEVEL.getOrDefault(mainHandItem, 0);
            final Integer offHandItemLightLevel = DynamicLightsClient.ITEM_BLOCK_LIGHT_LEVEL.getOrDefault(offHandItem, 0);
            return Math.max(mainHandItemLightLevel, offHandItemLightLevel);
        }

        return 0;
    }
}
