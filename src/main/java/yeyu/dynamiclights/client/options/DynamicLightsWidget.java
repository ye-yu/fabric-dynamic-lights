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

    public static List<OrderedText> wrapLines(MinecraftClient client, Text text) {
        return client.textRenderer.wrapLines(text, 200);
    }
    public final SimpleOption<?> DYNAMIC_LIGHTS_OPTIONS =
            new SimpleOption<>(
                    getLevelOptionName(),
                    client -> $ -> wrapLines(client, Text.translatable(getLevelOptionName() + ".desc")),
                    DynamicLightsWidget::valueToText,
                    new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(DynamicLightsLevel.values()), Codec.INT.xmap(
                            DynamicLightsLevel.OFF::byId, DynamicLightsLevel.OFF::getId
                    )),
                    DynamicLightsLevel.HEAVY,
                    DynamicLightsOptions::setLightsLevel
            );

    public final SimpleOption<?> DYNAMIC_LIGHTS_ENTITIES =
            new SimpleOption<>(
                    getEntitiesTickOptionName(),
                    client -> $ -> wrapLines(client, Text.translatable(getEntitiesTickOptionName() + ".desc")),
                    (optionText, value) -> Text.translatable("options.generic_value", Text.translatable(getEntitiesTickOptionName()), value.toString()),
                    new SimpleOption.ValidatingIntSliderCallbacks(3, 50), 3,
                    DynamicLightsOptions::setMaxEntitiesToTick);

    public final SimpleOption<?> DYNAMIC_LIGHTS_PERFORMANCE =
            new SimpleOption<>(
                    getPerformanceOptionName(),
                    client -> $ -> wrapLines(client, Text.translatable(getPerformanceOptionName() + ".desc")),
                    DynamicLightsWidget::valueToText,
                    new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(DynamicLightsTickDelays.values()), Codec.INT.xmap(
                            DynamicLightsTickDelays.SMOOTH::byId, DynamicLightsTickDelays.SMOOTH::getId
                    )),
                    DynamicLightsTickDelays.SMOOTH,
                    DynamicLightsOptions::setTickLevel
            );
    public final SimpleOption<?> DYNAMIC_LIGHTS_PRECISION =
            new SimpleOption<>(
                    getPrecisionOptionName(),
                    client -> $ -> wrapLines(client, Text.translatable(getPrecisionOptionName() + ".desc")),
                    DynamicLightsWidget::valueToText,
                    new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(DynamicLightsPrecision.values()), Codec.INT.xmap(
                            DynamicLightsPrecision.MINIMAL::byId, DynamicLightsPrecision.MINIMAL::getId
                    )),
                    DynamicLightsPrecision.MINIMAL,
                    DynamicLightsOptions::setPrecision
            );


    public final ArrayList<SimpleOption<?>> OPTIONS = new ArrayList<>() {{
            add(DYNAMIC_LIGHTS_ENTITIES);
            add(DYNAMIC_LIGHTS_OPTIONS);
            add(DYNAMIC_LIGHTS_PERFORMANCE);
            add(DYNAMIC_LIGHTS_PRECISION);
    }};

    public static final DynamicLightsWidget INSTANCE = new DynamicLightsWidget();

    private static Text valueToText(@SuppressWarnings("unused") Text optionText, Enum<?> value) {
        return Text.literal(value.name().toUpperCase(Locale.US));
    }
}
