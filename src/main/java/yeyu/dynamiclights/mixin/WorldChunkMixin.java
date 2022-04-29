package yeyu.dynamiclights.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import yeyu.dynamiclights.client.DynamicLightsStorage;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin implements BlockView {

    @Shadow
    @Final
    World world;

    @Override
    public int getLuminance(BlockPos pos) {
        if (!(this.world.isClient)) return this.getBlockState(pos).getLuminance();
        final BlockState state = this.getBlockState(pos.toImmutable());
        return state.isOpaque() ? state.getLuminance() : (int) Math.max(DynamicLightsStorage.getLightLevel(pos), state.getLuminance());
    }
}
