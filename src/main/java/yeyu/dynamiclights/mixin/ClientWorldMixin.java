package yeyu.dynamiclights.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yeyu.dynamiclights.client.DynamicLightsManager;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {


    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> supplier, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, supplier, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickEntities", at = @At("HEAD"))
    private void injectHeadTickEntities(CallbackInfo ci) {
        DynamicLightsManager.INSTANCE.tickEntities((ClientWorld) (Object) this);
    }
}
