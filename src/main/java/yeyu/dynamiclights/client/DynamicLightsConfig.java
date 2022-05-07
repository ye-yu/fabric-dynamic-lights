package yeyu.dynamiclights.client;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum DynamicLightsConfig implements Consumer<NbtCompound> {

    ENTITY("entity") {
        public final Defaults.String ID = new Defaults.String("id", null);
        public final Defaults.Boolean FORCE_DISABLE = new Defaults.Boolean("force disable", false);

        public final Defaults.Int LIGHT_DEFAULT_INT = new Defaults.Int("default light level", 0);
        public final Defaults.Int LIGHT_ENCHANTMENT_INT = new Defaults.Int("enchantment light level", 5);
        public final Defaults.Int LIGHT_FIRE_INT = new Defaults.Int("fire light level", 12);

        @Override
        public void accept(NbtCompound nbtCompound) {
            final String id = ID.get(nbtCompound);
            if (id == null) throw new RuntimeException("key 'id' has no value");
            final Boolean forceDisable = FORCE_DISABLE.get(nbtCompound);
            final Identifier identifier = Identifier.tryParse(id);
            final EntityType<?> entityType = Registry.ENTITY_TYPE.get(identifier);
            if (forceDisable) {
                DynamicLightsAttributes.registerEntityLightLevel(entityType, 0, 0, 0);
            }

            final Integer lightStrengthInt = LIGHT_DEFAULT_INT.get(nbtCompound);
            final Integer lightEnchantmentInt = LIGHT_ENCHANTMENT_INT.get(nbtCompound);
            final Integer lightFireInt = LIGHT_FIRE_INT.get(nbtCompound);
            DynamicLightsAttributes.registerEntityLightLevel(entityType, lightStrengthInt, lightEnchantmentInt, lightFireInt);
        }
    },
    ITEM("item") {
        public final Defaults.String ID = new Defaults.String("id", null);
        public final Defaults.Boolean FORCE_DISABLE = new Defaults.Boolean("force disable", false);
        public final Defaults.Int LIGHT_DEFAULT_INT = new Defaults.Int("default light level", 0);
        public final Defaults.Int LIGHT_ENCHANTMENT_INT = new Defaults.Int("enchantment light level", 5);
        public final Defaults.Int LIGHT_FIRE_INT = new Defaults.Int("fire light level", 12);

        @Override
        public void accept(NbtCompound nbtCompound) {
            final String id = ID.get(nbtCompound);
            if (id == null) throw new RuntimeException("key 'id' has no value");
            final Integer lightStrengthInt = LIGHT_DEFAULT_INT.get(nbtCompound);
            final Integer lightEnchantmentInt = LIGHT_ENCHANTMENT_INT.get(nbtCompound);
            final Integer lightFireInt = LIGHT_FIRE_INT.get(nbtCompound);
            final Identifier identifier = Identifier.tryParse(id);
            final Item matchedItem = Registry.ITEM.get(identifier);
            if (matchedItem == Items.AIR) throw new RuntimeException(String.format("key %s has invalid item", id));
            final Boolean forceDisable = FORCE_DISABLE.get(nbtCompound);
            if (forceDisable) {
                DynamicLightsAttributes.registerItemLightLevel(matchedItem, 0, 0, 0);
                return;
            }
            DynamicLightsAttributes.registerItemLightLevel(matchedItem, lightStrengthInt, lightEnchantmentInt, lightFireInt);
        }
    };

    public static final Logger LOGGER = LogManager.getLogger();
    public static HashMap<String, DynamicLightsConfig> REVERSE_MAP = null;
    private final String string;

    DynamicLightsConfig(String string) {
        this.string = string;
    }

    public static void bootstrap() {
        if (REVERSE_MAP == null) {
            REVERSE_MAP = Maps.newHashMap();
            Arrays.stream(DynamicLightsConfig.values()).forEach(e -> REVERSE_MAP.put(e.string, e));
        }
        DynamicLightsManager.INSTANCE.clear();

        // bootstrap resources directory
        for (final String filename : new String[]{
                "/config/player.yaml",
                "/config/glow_squid.yaml",
                "/config/beacon.yaml",
                "/config/creeper.yaml",
                "/config/tnt.yaml",
        }) {
            final InputStream resource = DynamicLightsClient.class.getResourceAsStream(filename);
            parse(resource, () -> String.format("Inner Resource: %s", filename));
        }



        // bootstrap run directory
        final File runDirectory = MinecraftClient.getInstance().runDirectory;
        final File targetDirectory = new File(runDirectory, "config/dynamic-lights");
        if (!targetDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            targetDirectory.mkdirs();
        }
        final File[] files = targetDirectory.listFiles();
        if (files == null) return;
        for (File file : files) {
            try {
                final FileInputStream fileInputStream = new FileInputStream(file);
                parse(fileInputStream, () -> "Custom file: " + file);

            } catch (FileNotFoundException e) {
                LOGGER.error("Error in parsing {}", file.toString());
                LOGGER.trace(e);
            }
        }
    }

    public static void parse(@Nullable InputStream inputStream, Supplier<String> nameSupplier) {
        LOGGER.info("Loading {}", nameSupplier.get());
        try {
            if (inputStream == null) return;
            final Scanner scanner = new Scanner(inputStream);
            NbtCompound nbtCompound = new NbtCompound();

            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                if (line.length() < 1) continue;
                if (line.startsWith("#")) continue;
                final String[] config = line.split(":", 2);
                if (config.length != 2) LOGGER.warn("Parse line error in {}, line {}", nameSupplier.get(), line);
                else nbtCompound.putString(config[0].trim(), config[1].trim());
            }

            if (!nbtCompound.contains("type"))
                throw new RuntimeException(String.format("Parse file error in %s: no 'type' parameter is provided!", nameSupplier.get()));
            final String type = nbtCompound.getString("type");
            if (!REVERSE_MAP.containsKey(type))
                throw new RuntimeException(String.format("Parse file error in %s: 'type' parameter is not known (%s)!", nameSupplier.get(), type));

            REVERSE_MAP.get(type).accept(nbtCompound);
        } catch (Exception e) {
            LOGGER.error("Cannot parse {}: {}", nameSupplier.get(), e);
        }
    }

    public static abstract class Defaults<T> {
        final java.lang.String key;
        final T defaultVal;

        protected Defaults(java.lang.String key, T defaultVal) {
            this.key = key;
            this.defaultVal = defaultVal;
        }

        abstract T get(NbtCompound nbt);

        public static class String extends Defaults<java.lang.String> {
            protected String(java.lang.String key, java.lang.String defaultVal) {
                super(key, defaultVal);
            }


            @Override
            java.lang.String get(NbtCompound nbt) {
                return nbt.contains(key) ? nbt.getString(key) : defaultVal;
            }
        }

        public static class Int extends Defaults<Integer> {

            protected Int(java.lang.String key, Integer defaultVal) {
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
            protected Float(java.lang.String key, java.lang.Float defaultVal) {
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

            protected Boolean(java.lang.String key, java.lang.Boolean defaultVal) {
                super(key, defaultVal);
            }


            @Override
            java.lang.Boolean get(NbtCompound nbt) {
                return nbt.contains(key) ? nbt.getString(key).equalsIgnoreCase("true") : defaultVal;
            }
        }
    }
}
