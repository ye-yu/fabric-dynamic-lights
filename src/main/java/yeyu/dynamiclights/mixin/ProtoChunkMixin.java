package yeyu.dynamiclights.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yeyu.dynamiclights.client.DynamicLightsClient;

import java.util.*;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin implements Chunk {
    @Shadow
    @Final
    private ChunkSection[] sections;
    @Shadow
    @Final
    private List<BlockPos> lightSources;
    @Shadow
    private volatile ChunkStatus status;
    @Shadow
    @Nullable
    private volatile LightingProvider lightingProvider;
    @Shadow
    @Final
    private Map<Heightmap.Type, Heightmap> heightmaps;

    @Shadow
    public abstract int getBottomY();

    @Inject(method = "setBlockState", at = @At("HEAD"), cancellable = true)
    private void injectHeadSetBlockState(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (j >= this.getBottomY() && j < this.getTopY()) {
            int l = this.getSectionIndex(j);
            if (this.sections[l] == WorldChunk.EMPTY_SECTION && state.isOf(Blocks.AIR)) {
                cir.setReturnValue(state);
            } else {

                if ((state.isOpaque() ? state.getLuminance() : Math.max(state.getLuminance(), DynamicLightsClient.getLightLevel(pos))) > 0) {
                    this.lightSources.add(new BlockPos((i & 15) + this.getPos().getStartX(), j, (k & 15) + this.getPos().getStartZ()));
                }

                ChunkSection chunkSection = this.getSection(l);
                BlockState blockState = chunkSection.setBlockState(i & 15, j & 15, k & 15, state);
                // TODO: enhance getLightLevel check
                if (this.status.isAtLeast(ChunkStatus.FEATURES) && state != blockState && (state.getOpacity(this, pos) != blockState.getOpacity(this, pos) || DynamicLightsClient.getLightLevel(pos) > 0 || state.hasSidedTransparency() || blockState.hasSidedTransparency())) {
                    Objects.requireNonNull(this.lightingProvider).checkBlock(pos);
                }

                EnumSet<Heightmap.Type> enumSet = this.getStatus().getHeightmapTypes();
                EnumSet<Heightmap.Type> enumSet2 = null;
                Iterator<Heightmap.Type> var12 = enumSet.iterator();

                Heightmap.Type type2;
                while (var12.hasNext()) {
                    type2 = var12.next();
                    Heightmap heightmap = this.heightmaps.get(type2);
                    if (heightmap == null) {
                        if (enumSet2 == null) {
                            enumSet2 = EnumSet.noneOf(Heightmap.Type.class);
                        }

                        enumSet2.add(type2);
                    }
                }

                if (enumSet2 != null) {
                    Heightmap.populateHeightmaps(this, enumSet2);
                }

                var12 = enumSet.iterator();

                while (var12.hasNext()) {
                    type2 = var12.next();
                    this.heightmaps.get(type2).trackUpdate(i & 15, j, k & 15, state);
                }

                cir.setReturnValue(blockState);
            }
        } else {
            cir.setReturnValue(Blocks.VOID_AIR.getDefaultState());
        }
    }
}
