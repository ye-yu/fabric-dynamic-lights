package yeyu.dynamiclights.client.options;

import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static yeyu.dynamiclights.client.options.DynamicLightsOptions.*;

public class DynamicLightsWidget {

    public static final DynamicLightsWidget INSTANCE = new DynamicLightsWidget();
    public final SimpleOption<?> DYNAMIC_LIGHTS_SPREADNESS =
            new SimpleOption<>(
                    getSpreadnessOptionName(),
                    client -> $ -> wrapLines(client, Text.translatable(getSpreadnessOptionName() + ".desc")),
                    DynamicLightsWidget::valueToText,
                    new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(DynamicLightsSpread.values()), Codec.INT.xmap(
                            DynamicLightsSpread.OFF::byId, DynamicLightsSpread.OFF::getId
                    )),
                    getSpreadness(),
                    DynamicLightsOptions::setSpreadness
            );

    public final SimpleOption<?> DYNAMIC_LIGHTS_ENTITIES =
            new SimpleOption<>(
                    getMaxEntitiesToTickOptionName(),
                    client -> $ -> wrapLines(client, Text.translatable(getMaxEntitiesToTickOptionName() + ".desc")),
                    (optionText, value) -> Text.translatable("options.generic_value", Text.translatable(getMaxEntitiesToTickOptionName()), value.toString()),
                    new SimpleOption.ValidatingIntSliderCallbacks(3, 50), getMaxEntitiesToTick(),
                    DynamicLightsOptions::setMaxEntitiesToTick);

    public final SimpleOption<?> DYNAMIC_LIGHTS_PERFORMANCE =
            new SimpleOption<>(
                    getPerformanceOptionName(),
                    client -> $ -> wrapLines(client, Text.translatable(getPerformanceOptionName() + ".desc")),
                    DynamicLightsWidget::valueToText,
                    new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(DynamicLightsTickDelays.values()), Codec.INT.xmap(
                            DynamicLightsTickDelays.SMOOTH::byId, DynamicLightsTickDelays.SMOOTH::getId
                    )),
                    getPerformance(),
                    DynamicLightsOptions::setPerformance
            );


    public final ArrayList<SimpleOption<?>> OPTIONS = new ArrayList<>() {{
        add(DYNAMIC_LIGHTS_SPREADNESS);
        add(DYNAMIC_LIGHTS_ENTITIES);
        add(DYNAMIC_LIGHTS_PERFORMANCE);
    }};

    public static List<OrderedText> wrapLines(MinecraftClient client, Text text) {
        return client.textRenderer.wrapLines(text, 200);
    }

    private static Text valueToText(@SuppressWarnings("unused") Text optionText, Enum<?> value) {
        return Text.literal(value.name().toUpperCase(Locale.US));
    }
}
