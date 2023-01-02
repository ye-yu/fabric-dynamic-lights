package yeyu.dynamiclights.client;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public record DynamicLightsAttributes(
        int strength,
        int enchantment,
        int fire
) {

    public static final HashMap<Item, DynamicLightsAttributes> ITEM2ATTRIBUTE = new HashMap<>();
    public static final HashMap<EntityType<?>, DynamicLightsAttributes> ENTITY2ATTRIBUTE = new HashMap<>();
    public static final DynamicLightsAttributes ITEM_DEFAULT = DynamicLightsAttributes.of(
            0, 5, 12
    );
    public static final DynamicLightsAttributes ENTITY_DEFAULT = DynamicLightsAttributes.of(
            0, 0, 0
    );

    public static DynamicLightsAttributes of(
            int strength,
            int enchantment,
            int fire
    ) {
        return new DynamicLightsAttributes(
                MathHelper.clamp(strength, 0, 15),
                MathHelper.clamp(enchantment, 0, 15),
                MathHelper.clamp(fire, 0, 15)
        );
    }

    public static void registerItemLightLevel(Item item, int strength, int enchantment, int fire) {
        final DynamicLightsAttributes dynamicLightsAttributes = of(strength, enchantment, fire);
        ITEM2ATTRIBUTE.put(item, dynamicLightsAttributes);
    }

    public static void registerItemLightLevel(Item item, int strength) {
        registerItemLightLevel(item, strength, ITEM_DEFAULT.enchantment(), ITEM_DEFAULT.fire());
    }

    public static void registerEntityLightLevel(EntityType<?> entity, int strength, int enchantment, int fire) {
        final DynamicLightsAttributes dynamicLightsAttributes = of(strength, enchantment, fire);
        ENTITY2ATTRIBUTE.put(entity, dynamicLightsAttributes);
    }
}
