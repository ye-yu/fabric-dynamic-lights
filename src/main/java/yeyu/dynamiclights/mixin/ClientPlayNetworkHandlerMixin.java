package yeyu.dynamiclights.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yeyu.dynamiclights.client.DynamicLightsStorage;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow
    private ClientWorld world;

    @Inject(method = "onItemPickupAnimation", at = @At("HEAD"))
    private void injectHeadOnItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo ci) {
        final int entityId = packet.getEntityId();
        Entity itemEntity = this.world.getEntityById(entityId);
        if (itemEntity == null) return;
        DynamicLightsStorage.scheduleUnlit(itemEntity.getBlockPos(), entityId);
    }
}
