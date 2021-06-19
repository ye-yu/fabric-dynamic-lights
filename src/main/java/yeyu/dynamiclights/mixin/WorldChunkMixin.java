package yeyu.dynamiclights.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yeyu.dynamiclights.client.DynamicLightsClient;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin implements Chunk {

    @Shadow
    @Final
    private ChunkPos pos;

    @Shadow
    public abstract int getBottomY();

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Inject(method = "getLightSourcesStream", at = @At("HEAD"), cancellable = true)
    private void injectHeadGetLightSourcesStream(CallbackInfoReturnable<Stream<BlockPos>> cir) {
        cir.setReturnValue(StreamSupport.stream(BlockPos.iterate(this.pos.getStartX(), this.getBottomY(), this.pos.getStartZ(), this.pos.getEndX(), this.getTopY() - 1, this.pos.getEndZ()).spliterator(), false).filter((blockPos) -> {
            final BlockState state = this.getBlockState(blockPos);
            return (state.isOpaque() ? state.getLuminance() : Math.max(state.getLuminance(), DynamicLightsClient.getLightLevel(blockPos))) != 0;
        }));
    }
}
