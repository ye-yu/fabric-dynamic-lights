package yeyu.dynamiclights.client.options;

import com.google.common.base.Suppliers;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;

public enum DynamicLightsTickDelays {
    SMOOTH(1),
    EASE(2),
    FASTER(3),
    FASTEST(4);

    public final int SKIP_EVERY;

    DynamicLightsTickDelays(int skipEvery) {
        SKIP_EVERY = skipEvery;
    }

    @Override
    public String toString() {
        return this.name().toUpperCase(Locale.US);
    }

    public static final HashMap<String, DynamicLightsTickDelays> STR2OBJ = new HashMap<>() {{
        for (DynamicLightsTickDelays value : DynamicLightsTickDelays.values()) {
            put(value.toString(), value);
        }
    }};

}
