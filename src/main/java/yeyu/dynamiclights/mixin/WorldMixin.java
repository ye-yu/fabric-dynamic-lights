package yeyu.dynamiclights.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yeyu.dynamiclights.client.DynamicLightsClient;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess {

    @Shadow
    @Final
    public boolean isClient;

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Shadow
    public abstract Profiler getProfiler();

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("RETURN"), cancellable = true)
    private void injectReturnSetBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        if (!isClient || !cir.getReturnValue() || state.isOpaque() || (flags & Block.SKIP_LIGHTING_UPDATES) != 0
                || DynamicLightsClient.getLightLevel(pos) <= 0) return;
        this.getProfiler().push("queueCheckLight");
        this.getChunkManager().getLightingProvider().checkBlock(pos);
        this.getProfiler().pop();
    }
}
