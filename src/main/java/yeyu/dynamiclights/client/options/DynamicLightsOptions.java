package yeyu.dynamiclights.client.options;

import com.google.common.io.Files;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;

public class DynamicLightsOptions {
    private static DynamicLightsLevel lightsLevel = DynamicLightsLevel.HEAVY;
    private static int maxEntitiesTick = 3;
    private static DynamicLightsTickDelays performance = DynamicLightsTickDelays.SMOOTH;
    private static DynamicLightsPrecision precision = DynamicLightsPrecision.MINIMAL;

    public static DynamicLightsLevel getLightsLevel() {
        return lightsLevel;
    }

    public static String getLightsLevelOptionName() {
        return "dynamiclights.lights_level";
    }

    public static String getMaxEntitiesToTickOptionName() {
        return "dynamiclights.max_nearest_entities";
    }

    public static String getPerformanceOptionName() {
        return "dynamiclights.performance";
    }

    public static String getPrecisionOptionName() {
        return "dynamiclights.precision";
    }

    public static void setLightsLevel(int level) {
        setLightsLevel(tryAccessArray(DynamicLightsLevel.values(), level));
    }

    public static void setLightsLevel(String level) {
        setLightsLevel(DynamicLightsLevel.STR2OBJ.getOrDefault(level, lightsLevel));
    }

    public static void setLightsLevel(DynamicLightsLevel level) {
        // TODO: if DynamicLightsLevel changed, then schedule chunk rebuild instead to reset bp light level values
        lightsLevel = level;
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
    public static void setTickLevel(final int level) {
        performance = tryAccessArray(DynamicLightsTickDelays.values(),  level);
    }

    public static DynamicLightsPrecision getPrecision() {
        return precision;
    }

    public static void setPrecision(int precision) {
        DynamicLightsOptions.precision = tryAccessArray(DynamicLightsPrecision.values(), precision);
    }

    public static void setPrecision(String precision) {
        DynamicLightsOptions.precision = DynamicLightsPrecision.STR2OBJ.getOrDefault(precision, DynamicLightsOptions.precision);
    }

    public static final String OPTIONS_FILENAME = "dynamic-lights.yaml";
    public static final String LANG_FILEPATH = "/assets/minecraft/lang/en_us.json";

    public static HashMap<String, String> getResourceLangFile() throws IOException {
        final HashMap<String, String> map = new HashMap<>();
        try (InputStream resourceAsStream = DynamicLightsOptions.class.getResourceAsStream(LANG_FILEPATH)) {
            if (resourceAsStream == null) return map;
            final byte[] bytes = resourceAsStream.readAllBytes();
            final String json = new String(bytes, StandardCharsets.UTF_8);
            final JsonReader jsonReader = new JsonReader(new StringReader(json));
            jsonReader.beginObject();
            while(jsonReader.hasNext()) {
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
            final String lightLevelOptionDescription = resourceLangFile.getOrDefault(getLightsLevelOptionName() + ".desc", getLightsLevelOptionName() + ".desc");
            final String maxEntitiesToTickOptionDescription = resourceLangFile.getOrDefault(getMaxEntitiesToTickOptionName() + ".desc", getMaxEntitiesToTickOptionName() + ".desc");
            final String performanceOptionDescription = resourceLangFile.getOrDefault(getPerformanceOptionName() + ".desc", getPerformanceOptionName() + ".desc");
            final String precisionOptionDescription = resourceLangFile.getOrDefault(getPrecisionOptionName() + ".desc", getPrecisionOptionName() + ".desc");

            printWriter.printf("# %s%n", lightLevelOptionDescription);
            printWriter.printf("%s: %s%n%n", getLightsLevelOptionName(), getLightsLevel());

            printWriter.printf("# %s%n", maxEntitiesToTickOptionDescription);
            printWriter.printf("%s: %s%n%n", getMaxEntitiesToTickOptionName(), getMaxEntitiesToTick());

            printWriter.printf("# %s%n", performanceOptionDescription);
            printWriter.printf("%s: %s%n%n", getPerformanceOptionName(), getPerformance());

            printWriter.printf("# %s%n", precisionOptionDescription);
            printWriter.printf("%s: %s%n%n", getPrecisionOptionName(), getPrecision());
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

    public static <T> T tryAccessArray(@NotNull T[] arr, int at) {
        return arr[Math.floorMod(at, arr.length)];
    }
    public static void readSettings() throws IOException {
        final Path optionsFilePath = getOptionsFilePath();
        if (!optionsFilePath.toFile().exists()) writeSettings();
        final Scanner scanner = new Scanner(optionsFilePath);
        final HashMap<String, String> defaultSettings = new HashMap<>();
        defaultSettings.put(getLightsLevelOptionName(), getLightsLevel().toString());
        defaultSettings.put(getMaxEntitiesToTickOptionName(), getMaxEntitiesToTick() + "");
        defaultSettings.put(getPerformanceOptionName(), getPerformance().toString());
        defaultSettings.put(getPrecisionOptionName(), getPrecision().toString());

        while(scanner.hasNextLine()) {
            final String s = scanner.nextLine().trim();
            if (s.isEmpty() || s.startsWith("#")) continue;
            final String[] keyValuePair = s.split(":", 2);
            defaultSettings.put(keyValuePair[0].trim(), keyValuePair[1].trim());
        }

        setLightsLevel(defaultSettings.getOrDefault(getLightsLevelOptionName(), getLightsLevel().toString()));
        setMaxEntitiesToTick(tryGetDefaultWrap(defaultSettings, getMaxEntitiesToTickOptionName(), getMaxEntitiesToTick() + "", Integer::parseInt));
        setPerformance(defaultSettings.getOrDefault(getPerformanceOptionName(), getPerformance().toString()));
        setPrecision(defaultSettings.getOrDefault(getPrecisionOptionName(), getPrecision().toString()));
    }
}
