package yeyu.dynamiclights.mixin.option;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import net.minecraft.client.option.GameOptions;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yeyu.dynamiclights.client.DynamicLightsOption;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Shadow
    @Final
    static Logger LOGGER;
    @Shadow
    @Final
    private static Splitter COLON_SPLITTER;
    @Shadow
    @Final
    private File optionsFile;

    @Inject(method = "write", at = @At("HEAD"))
    private void injectHeadWrite(CallbackInfo ci) throws FileNotFoundException {
        final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));
        printWriter.print(DynamicLightsOption.OPTION_NAME);
        printWriter.print(":");
        printWriter.println(DynamicLightsOption.getCurrentOption().ordinal());
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void injectHeadLoad(CallbackInfo ci) throws IOException {
        NbtCompound nbtCompound = new NbtCompound();

        //noinspection UnstableApiUsage
        try (BufferedReader bufferedReader = Files.newReader(this.optionsFile, Charsets.UTF_8)) {
            bufferedReader.lines().forEach((line) -> {
                try {
                    Iterator<String> iterator = COLON_SPLITTER.split(line).iterator();
                    nbtCompound.putString(iterator.next(), iterator.next());
                } catch (Exception var3) {
                    LOGGER.warn("Skipping bad option: {}", line);
                }
            });
        }

        if (nbtCompound.contains(DynamicLightsOption.OPTION_NAME)) {
            try {
                DynamicLightsOption.setCurrentOption(nbtCompound.getInt(DynamicLightsOption.OPTION_NAME));
            } catch (Exception e) {
                LOGGER.warn("Skipping bad option: " + DynamicLightsOption.OPTION_NAME + " {}", nbtCompound.get(DynamicLightsOption.OPTION_NAME));
            }
        }
    }
}
