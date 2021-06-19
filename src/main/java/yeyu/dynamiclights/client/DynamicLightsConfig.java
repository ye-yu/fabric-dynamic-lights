package yeyu.dynamiclights.client;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum DynamicLightsConfig implements Consumer<NbtCompound> {
    ENTITY("entity") {
        public final Defaults.String ID = new Defaults.String("id", null);
        public final Defaults.Boolean OVERWRITE = new Defaults.Boolean("overwrite", true);
        public final Defaults.Boolean LIGHT_STRENGTH_BY_ITEM = new Defaults.Boolean("light strength by item", false);
        public final Defaults.Int LIGHT_STRENGTH_INT = new Defaults.Int("light strength level", 0);
        public final Defaults.Float LIGHT_SOURCE_OFFSET = new Defaults.Float("light source offset", 1f);
        @Override
        public void accept(NbtCompound nbtCompound) {
            final String id = ID.get(nbtCompound);
            if (id == null) throw new RuntimeException("key 'id' has no value");

            final Boolean lightStrengthByItem = LIGHT_STRENGTH_BY_ITEM.get(nbtCompound);
            final Boolean overwrite = OVERWRITE.get(nbtCompound);
            final Float lightSourceOffSet = LIGHT_SOURCE_OFFSET.get(nbtCompound);
            final Integer lightStrengthInt = LIGHT_STRENGTH_INT.get(nbtCompound);

            if (overwrite) {
                DynamicLightsManager.INSTANCE.registerEntityTick(Identifier.tryParse(id), getEntityClientWorldBiConsumer(lightStrengthByItem, lightSourceOffSet, lightStrengthInt));
            } else {
                DynamicLightsManager.INSTANCE.appendEntityTick(Identifier.tryParse(id), getEntityClientWorldBiConsumer(lightStrengthByItem, lightSourceOffSet, lightStrengthInt));
            }
        }

        @NotNull
        private BiConsumer<Entity, ClientWorld> getEntityClientWorldBiConsumer(Boolean lightStrengthByItem, Float lightSourceOffSet, Integer lightStrengthInt) {
            return (entity, clientWorld) -> {
                if (lightStrengthByItem) {
                    DynamicLightsUtils.handleEntityLightsByItem(entity, clientWorld, lightSourceOffSet);
                } else if (lightStrengthInt > 0) {
                    DynamicLightsUtils.handleEntity(entity, clientWorld, lightStrengthInt);
                }
            };
        }
    };

    private final String string;

    DynamicLightsConfig(String string) {
        this.string = string;
    }

    public static HashMap<String, DynamicLightsConfig> REVERSE_MAP = null;
    public static Logger LOGGER = LogManager.getLogger();

    public static void bootstrap() {
        if (REVERSE_MAP == null) {
            REVERSE_MAP = Maps.newHashMap();
            Arrays.stream(DynamicLightsConfig.values()).forEach(e -> REVERSE_MAP.put(e.string, e));
        }

        // bootstrap resources directory
        {
            final InputStream resource = DynamicLightsClient.class.getResourceAsStream("/config/player.yaml");
            parse(resource, () -> "Inner Resource: /config/player.yaml");
        }
        {
            final InputStream resource = DynamicLightsClient.class.getResourceAsStream("/config/glow_squid.yaml");
            parse(resource, () -> "Inner Resource: /config/glow_squid.yaml");
        }

        // bootstrap run directory
        final File runDirectory = MinecraftClient.getInstance().runDirectory;
        final File targetDirectory = new File(runDirectory, "config/dynamic-lights");
        final File[] files = targetDirectory.listFiles();
        if (files == null) return;
        DynamicLightsManager.INSTANCE.clear();
    }

    public static void parse(@Nullable InputStream inputStream, Supplier<String> nameSupplier) {
        if (inputStream == null) return;
        final Scanner scanner = new Scanner(inputStream);
        NbtCompound nbtCompound = new NbtCompound();

        while(scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            if (line.length() < 1) continue;
            if (line.startsWith("#")) continue;
            final String[] config = line.split(":", 2);
            if (config.length != 2) LOGGER.warn("Parse line error in {}, line {}", nameSupplier.get(), line);
            else nbtCompound.putString(config[0].trim(), config[1].trim());
        }

        if (!nbtCompound.contains("type")) throw new RuntimeException(String.format("Parse file error in %s: no 'type' parameter is provided!", nameSupplier.get()));
        final String type = nbtCompound.getString("type");
        if (!REVERSE_MAP.containsKey(type)) throw new RuntimeException(String.format("Parse file error in %s: 'type' parameter is not known (%s)!", nameSupplier.get(), type));

        REVERSE_MAP.get(type).accept(nbtCompound);
    }

    public static abstract class Defaults<T> {
        public static class String extends Defaults<java.lang.String> {
            protected String(java.lang.String key,  java.lang.String defaultVal) {
                super(key, defaultVal);
            }

            
            @Override
            java.lang.String get(NbtCompound nbt) {
                return nbt.contains(key) ? nbt.getString(key) : defaultVal;
            }
        }

        public static class Int extends Defaults<Integer> {

            protected Int(java.lang.String key,  Integer defaultVal) {
                super(key, defaultVal);
            }

            
            @Override
            Integer get(NbtCompound nbt) {
                try {
                    return Integer.parseInt(nbt.getString(key));
                } catch (Exception _ignored) {
                    return defaultVal;
                }
            }
        }

        public static class Float extends Defaults<java.lang.Float> {
            protected Float(java.lang.String key,  java.lang.Float defaultVal) {
                super(key, defaultVal);
            }

            
            @Override
            java.lang.Float get(NbtCompound nbt) {
                try {
                    return java.lang.Float.parseFloat(nbt.getString(key));
                } catch (Exception _ignored) {
                    return defaultVal;
                }
            }
        }

        public static class Boolean extends Defaults<java.lang.Boolean> {

            protected Boolean(java.lang.String key,  java.lang.Boolean defaultVal) {
                super(key, defaultVal);
            }

            
            @Override
            java.lang.Boolean get(NbtCompound nbt) {
                return nbt.contains(key) ? nbt.getString(key).equalsIgnoreCase("true") : defaultVal;
            }
        }

        final java.lang.String key;
        
        final T defaultVal;

        protected Defaults(java.lang.String key,  T defaultVal) {
            this.key = key;
            this.defaultVal = defaultVal;
        }

        
        abstract T get(NbtCompound nbt);
    }
}
