package yeyu.dynamiclights.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import yeyu.dynamiclights.client.DynamicLightsClient;

@Mixin(value = {
        ChunkCache.class,
        World.class,
        ServerWorld.class,
        ChunkRegion.class,
        ChunkRendererRegion.class,
        ProtoChunk.class,
        WorldChunk.class,
        EmptyBlockView.class
})
public abstract class BlockViewMixin implements BlockView {

    @Override
    public int getLuminance(BlockPos pos) {
        final BlockState state = this.getBlockState(pos);
        return state.isOpaque() ? state.getLuminance() : (int) Math.max(DynamicLightsClient.getLightLevel(pos), state.getLuminance());
    }
}
