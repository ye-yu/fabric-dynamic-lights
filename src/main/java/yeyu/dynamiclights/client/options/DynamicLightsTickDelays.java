package yeyu.dynamiclights.client.options;

import com.google.common.base.Suppliers;
import yeyu.dynamiclights.SerializableEnum;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;

public enum DynamicLightsTickDelays implements SerializableEnum<DynamicLightsTickDelays> {
    SMOOTH(1),
    EASE(2),
    FASTER(3),
    FASTEST(4);

    public static final Supplier<HashMap<String, DynamicLightsTickDelays>> STR2OBJ = Suppliers.memoize(() -> new HashMap<String, DynamicLightsTickDelays>() {{
        for (DynamicLightsTickDelays value : DynamicLightsTickDelays.values()) {
            put(value.name().toUpperCase(Locale.US), value);
        }
    }})::get;
    public final int SKIP_EVERY;

    DynamicLightsTickDelays(int skipEvery) {
        SKIP_EVERY = skipEvery;
    }

    @Override
    public DynamicLightsTickDelays[] getValues() {
        return DynamicLightsTickDelays.values();
    }
}
