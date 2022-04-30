package yeyu.dynamiclights.mixin.option;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yeyu.dynamiclights.client.options.DynamicLightsOptions;
import yeyu.dynamiclights.client.options.DynamicLightsPrecision;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    private static final Logger LOGGER = LogManager.getLogger();

    @Inject(method = "write", at = @At("HEAD"))
    private void injectHeadWrite(CallbackInfo ci) throws IOException {
        final File runDirectory = MinecraftClient.getInstance().runDirectory;
        final File targetDirectory = new File(runDirectory, "config/dynamic-lights");
        final File file = targetDirectory.toPath().resolve("options.yaml").toFile();
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        }
        final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(java.nio.file.Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8));
        printWriter.print(DynamicLightsOptions.DYNAMIC_LIGHTS_OPTIONS.getLeft());
        printWriter.print(":");
        printWriter.println(DynamicLightsOptions.getCurrentOption().ordinal());

        printWriter.print(DynamicLightsOptions.DYNAMIC_LIGHTS_ENTITIES.getLeft());
        printWriter.print(":");
        printWriter.println(DynamicLightsOptions.getMaxEntitiesToTick());

        printWriter.print(DynamicLightsOptions.DYNAMIC_LIGHTS_PERFORMANCE.getLeft());
        printWriter.print(":");
        printWriter.println(DynamicLightsOptions.getTickLevel());

        printWriter.print(DynamicLightsOptions.DYNAMIC_LIGHTS_PRECISION.getLeft());
        printWriter.print(":");
        printWriter.println(DynamicLightsOptions.getPrecision());

        printWriter.close();
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void injectHeadLoad(CallbackInfo ci) throws IOException {
        NbtCompound nbtCompound = new NbtCompound();
        final File runDirectory = MinecraftClient.getInstance().runDirectory;
        final File targetDirectory = new File(runDirectory, "config/dynamic-lights");
        final File file = targetDirectory.toPath().resolve("options.yaml").toFile();
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        }

        //noinspection UnstableApiUsage
        try (BufferedReader bufferedReader = Files.newReader(file, Charsets.UTF_8)) {
            bufferedReader.lines().forEach((line) -> {
                try {
                    final String[] split = line.split(":", 2);
                    nbtCompound.putString(split[0], split[1]);
                } catch (Exception var3) {
                    LOGGER.warn("Skipping bad option: {}", line);
                }
            });
        }
        final String lightsOption = DynamicLightsOptions.DYNAMIC_LIGHTS_OPTIONS.getLeft();
        if (nbtCompound.contains(lightsOption)) {
            try {
                DynamicLightsOptions.setCurrentOption(Integer.parseInt(nbtCompound.getString(lightsOption)));
            } catch (Exception e) {
                LOGGER.warn("Skipping bad option: " + lightsOption + " {}", nbtCompound.get(lightsOption));
            }
        }

        final String entitiesOption = DynamicLightsOptions.DYNAMIC_LIGHTS_ENTITIES.getLeft();
        if (nbtCompound.contains(entitiesOption)) {
            try {
                DynamicLightsOptions.setMaxEntitiesToTick(Integer.parseInt(nbtCompound.getString(entitiesOption)));
            } catch (Exception e) {
                LOGGER.warn("Skipping bad option: " + entitiesOption + " {}", nbtCompound.get(entitiesOption));
            }
        }

        final String ticksLevel = DynamicLightsOptions.DYNAMIC_LIGHTS_PERFORMANCE.getLeft();
        if (nbtCompound.contains(ticksLevel)) {
            try {
                DynamicLightsOptions.setTickLevel(Integer.parseInt(nbtCompound.getString(ticksLevel)));
            } catch (Exception e) {
                LOGGER.warn("Skipping bad option: " + ticksLevel + " {}", nbtCompound.get(ticksLevel));
            }
        }

        final String precision = DynamicLightsOptions.DYNAMIC_LIGHTS_PRECISION.getLeft();
        if (nbtCompound.contains(precision)) {
            try {
                final var precisionObj = DynamicLightsPrecision.STR2OBJ.get().getOrDefault(nbtCompound.getString(precision).toUpperCase(Locale.US), DynamicLightsPrecision.MINIMAL);
                DynamicLightsOptions.setPrecision(precisionObj);
            } catch (Exception e) {
                LOGGER.warn("Skipping bad option: " + precision + " {}", nbtCompound.get(precision));
            }
        }

    }
}
