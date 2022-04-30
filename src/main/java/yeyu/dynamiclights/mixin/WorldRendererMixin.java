package yeyu.dynamiclights.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yeyu.dynamiclights.client.DynamicLightsStorage;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(
            method = "getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void injectHeadCancellableGetLightMapCoordinates(BlockRenderView world, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (state.isOpaqueFullCube(world, pos)) return;
        if (state.hasEmissiveLighting(world, pos)) return;
        final double dynamicLightLevel = DynamicLightsStorage.getLightLevel(pos);
        final Integer vanillaLightMap = cir.getReturnValue();
        final int blockLightCoordinates = vanillaLightMap >> 4 & 0xffff / 16;
        final int light = Math.min(0xff, (int) (16 * Math.max(dynamicLightLevel, blockLightCoordinates)));
        cir.setReturnValue(vanillaLightMap & 0xfff0_0000 | light);
    }
}
