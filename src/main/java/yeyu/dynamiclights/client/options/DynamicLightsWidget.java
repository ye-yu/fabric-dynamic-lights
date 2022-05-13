package yeyu.dynamiclights.client.options;

import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.Option;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static yeyu.dynamiclights.client.options.DynamicLightsOptions.*;

@SuppressWarnings("unused")
public class DynamicLightsWidget {

    public static final DynamicLightsWidget INSTANCE = new DynamicLightsWidget();
    public final Option DYNAMIC_LIGHTS_ENTITIES =
            new DoubleOption(getMaxEntitiesToTickOptionName(),
                    4,
                    50,
                    2,
                    $ -> (double) getMaxEntitiesToTick(),
                    ($, value) -> setMaxEntitiesToTick(value.intValue()),
                    ($, option) -> {
                        int d = getMaxEntitiesToTick();
                        return new TranslatableText("options.generic_value", new TranslatableText(getMaxEntitiesToTickOptionName()), d);
                    },
                    (client) -> client.textRenderer.wrapLines(new TranslatableText(getMaxEntitiesToTickOptionName() + ".desc"), 200));

    public final Option DYNAMIC_LIGHTS_DISTANCE =
            new DoubleOption(getDistanceOptionName(),
                    10,
                    80,
                    10,
                    $ -> getDistance(),
                    ($, value) -> setDistance(value),
                    ($, option) -> {
                        final int d = (int) getDistance();
                        return new TranslatableText("options.generic_value", new TranslatableText(getDistanceOptionName()), d > 79.99 ? "MAX" : d);
                    },
                    (client) -> client.textRenderer.wrapLines(new TranslatableText(getDistanceOptionName() + ".desc"), 200));

    public final Option DYNAMIC_LIGHTS_PERFORMANCE =
            CyclingOption.create(getPerformanceOptionName(),
                    () -> IntStream.range(0, DynamicLightsTickDelays.values().length).boxed().collect(Collectors.toList()),
                    (level) -> new LiteralText(DynamicLightsTickDelays.values()[level].name()),
                    ($) -> getPerformance().ordinal(),
                    ($, $$, level) -> setTickLevel(level));
    public final Option DYNAMIC_LIGHTS_SPREAD =
            CyclingOption.create(getSpreadnessOptionName(),
                    () -> IntStream.range(0, DynamicLightsSpread.values().length).boxed().collect(Collectors.toList()),
                    (level) -> new LiteralText(DynamicLightsSpread.values()[level].name()),
                    ($) -> getSpreadness().ordinal(),
                    ($, $$, level) -> setSpreadness(level));
    public final ArrayList<Option> OPTIONS = new ArrayList<>() {{
        add(DYNAMIC_LIGHTS_ENTITIES);
        add(DYNAMIC_LIGHTS_DISTANCE);
        add(DYNAMIC_LIGHTS_PERFORMANCE);
        add(DYNAMIC_LIGHTS_SPREAD);
    }};
}
