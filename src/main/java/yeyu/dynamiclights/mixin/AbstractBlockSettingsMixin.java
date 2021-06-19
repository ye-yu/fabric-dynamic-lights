package yeyu.dynamiclights.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yeyu.dynamiclights.client.DynamicLightsClient;

@Mixin(AbstractBlock.Settings.class)
public class AbstractBlockSettingsMixin {

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "method_26239(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z", at = @At("HEAD"), cancellable = true)
    private static void injectHeadDefaultAllowSpawningPredicate(BlockState bs, BlockView bv, BlockPos bp, EntityType<?> et, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(bs.isSideSolidFullSquare(bv, bp, Direction.UP) && (bs.isOpaque() ? bs.getLuminance() : Math.max(DynamicLightsClient.getLightLevel(bp), bs.getLuminance())) < 14);
    }
}