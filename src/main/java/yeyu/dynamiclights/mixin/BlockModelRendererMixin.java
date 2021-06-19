package yeyu.dynamiclights.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yeyu.dynamiclights.client.DynamicLightStorage;

import java.util.Random;

@Mixin(BlockModelRenderer.class)
public abstract class BlockModelRendererMixin {
    @Shadow
    public abstract boolean renderSmooth(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack buffer, VertexConsumer vertexConsumer, boolean cull, Random random, long seed, int overlay);

    @Shadow
    public abstract boolean renderFlat(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack buffer, VertexConsumer vertexConsumer, boolean cull, Random random, long l, int i);

    @Inject(
            method = "render(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLjava/util/Random;JI)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectHeadRender(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrix, VertexConsumer vertexConsumer, boolean cull, Random random, long seed, int overlay, CallbackInfoReturnable<Boolean> cir) {
        boolean bl = MinecraftClient.isAmbientOcclusionEnabled()
                && (int) (state.isOpaque() ? state.getLuminance() : Math.max(state.getLuminance(), DynamicLightStorage.getLightLevel(pos))) == 0
                && model.useAmbientOcclusion();
        Vec3d vec3d = state.getModelOffset(world, pos);
        matrix.translate(vec3d.x, vec3d.y, vec3d.z);

        try {
            final boolean rval = bl ? this.renderSmooth(world, model, state, pos, matrix, vertexConsumer, cull, random, seed, overlay) : this.renderFlat(world, model, state, pos, matrix, vertexConsumer, cull, random, seed, overlay);
            cir.setReturnValue(rval);
        } catch (Throwable var17) {
            CrashReport crashReport = CrashReport.create(var17, "Tesselating block model");
            CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, world, pos, state);
            crashReportSection.add("Using AO", bl);
            throw new CrashException(crashReport);
        }
    }
}
