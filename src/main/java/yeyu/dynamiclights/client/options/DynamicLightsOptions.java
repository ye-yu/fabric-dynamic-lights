package yeyu.dynamiclights.client.options;

import com.google.common.io.Files;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import yeyu.dynamiclights.client.DynamicLightsManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;

public class DynamicLightsOptions {
    public static final String OPTIONS_FILENAME = "dynamic-lights.yaml";
    public static final String LANG_FILEPATH = "/assets/minecraft/lang/en_us.json";
    private static int maxEntitiesTick = 3;
    private static DynamicLightsTickDelays performance = DynamicLightsTickDelays.SMOOTH;
    private static DynamicLightsSpread spreadness = DynamicLightsSpread.FAST;

    public static String getMaxEntitiesToTickOptionName() {
        return "dynamiclights.max_nearest_entities";
    }

    public static String getPerformanceOptionName() {
        return "dynamiclights.performance";
    }

    public static String getSpreadnessOptionName() {
        return "dynamiclights.spreadness";
    }

    public static int getMaxEntitiesToTick() {
        return maxEntitiesTick;
    }

    public static void setMaxEntitiesToTick(int max) {
        maxEntitiesTick = max;
    }

    public static DynamicLightsTickDelays getPerformance() {
        return performance;
    }

    public static void setPerformance(final String level) {
        performance = DynamicLightsTickDelays.STR2OBJ.getOrDefault(level.toUpperCase(Locale.US), performance);
    }

    public static void setPerformance(final DynamicLightsTickDelays level) {
        performance = level;
    }

    public static DynamicLightsSpread getSpreadness() {
        return spreadness;
    }

    public static void setSpreadness(String spreadness) {
        DynamicLightsOptions.spreadness = DynamicLightsSpread.STR2OBJ.getOrDefault(spreadness, DynamicLightsOptions.spreadness);
    }

    public static void setSpreadness(DynamicLightsSpread spreadness) {
        DynamicLightsOptions.spreadness = spreadness;
        DynamicLightsManager.INSTANCE.resetLights();
    }

    public static HashMap<String, String> getResourceLangFile() throws IOException {
        final HashMap<String, String> map = new HashMap<>();
        try (InputStream resourceAsStream = DynamicLightsOptions.class.getResourceAsStream(LANG_FILEPATH)) {
            if (resourceAsStream == null) return map;
            final byte[] bytes = resourceAsStream.readAllBytes();
            final String json = new String(bytes, StandardCharsets.UTF_8);
            final JsonReader jsonReader = new JsonReader(new StringReader(json));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                try {
                    final String k = jsonReader.nextName();
                    final String v = jsonReader.nextString();
                    map.put(k, v);
                } catch (Exception ignored) {
                }
            }
        }
        return map;

    }

    public static void writeSettings() throws IOException {
        final Path optionsFilePath = getOptionsFilePath();
        final HashMap<String, String> resourceLangFile = getResourceLangFile();
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(java.nio.file.Files.newOutputStream(optionsFilePath), StandardCharsets.UTF_8))) {
            final String maxEntitiesToTickOptionDescription = resourceLangFile.getOrDefault(getMaxEntitiesToTickOptionName() + ".desc", getMaxEntitiesToTickOptionName() + ".desc");
            final String performanceOptionDescription = resourceLangFile.getOrDefault(getPerformanceOptionName() + ".desc", getPerformanceOptionName() + ".desc");
            final String spreadnessOptionDescription = resourceLangFile.getOrDefault(getSpreadnessOptionName() + ".desc", getSpreadnessOptionName() + ".desc");

            printWriter.printf("# %s%n", maxEntitiesToTickOptionDescription);
            printWriter.printf("%s: %s%n%n", getMaxEntitiesToTickOptionName(), getMaxEntitiesToTick());

            printWriter.printf("# %s%n", performanceOptionDescription);
            printWriter.printf("%s: %s%n%n", getPerformanceOptionName(), getPerformance());

            printWriter.printf("# %s%n", spreadnessOptionDescription);
            printWriter.printf("%s: %s", getSpreadnessOptionName(), getSpreadness());
        }
    }

    @NotNull
    @SuppressWarnings("UnstableApiUsage")
    private static Path getOptionsFilePath() throws IOException {
        final Path runPath = Objects.requireNonNull(MinecraftClient.getInstance()).runDirectory.toPath();
        final Path optionsFilePath = runPath.resolve("config").resolve(OPTIONS_FILENAME);
        Files.createParentDirs(optionsFilePath.toFile());
        return optionsFilePath;
    }

    public static <T, V, W> W tryGetDefaultWrap(final HashMap<T, V> map, T key, V def, Function<V, W> mapper) {
        try {
            final V value = map.getOrDefault(key, def);
            return mapper.apply(value);
        } catch (Exception any) {
            return mapper.apply(def);
        }
    }

    public static void readSettings() throws IOException {
        final Path optionsFilePath = getOptionsFilePath();
        if (!optionsFilePath.toFile().exists()) writeSettings();
        final Scanner scanner = new Scanner(optionsFilePath);
        final HashMap<String, String> defaultSettings = new HashMap<>();
        defaultSettings.put(getMaxEntitiesToTickOptionName(), getMaxEntitiesToTick() + "");
        defaultSettings.put(getPerformanceOptionName(), getPerformance().toString());
        defaultSettings.put(getSpreadnessOptionName(), getSpreadness().toString());

        while (scanner.hasNextLine()) {
            final String s = scanner.nextLine().trim();
            if (s.isEmpty() || s.startsWith("#")) continue;
            final String[] keyValuePair = s.split(":", 2);
            defaultSettings.put(keyValuePair[0].trim(), keyValuePair[1].trim());
        }

        setMaxEntitiesToTick(tryGetDefaultWrap(defaultSettings, getMaxEntitiesToTickOptionName(), getMaxEntitiesToTick() + "", Integer::parseInt));
        setPerformance(defaultSettings.getOrDefault(getPerformanceOptionName(), getPerformance().toString()));
        setSpreadness(defaultSettings.getOrDefault(getSpreadnessOptionName(), getSpreadness().toString()));
    }
}
