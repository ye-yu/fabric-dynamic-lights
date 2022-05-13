package yeyu.dynamiclights.client.options;

import java.util.HashMap;
import java.util.Locale;

public enum DynamicLightsTickDelays {
    SMOOTH(1),
    FAST(2),
    FASTER(3),
    FASTEST(4);

    public static final HashMap<String, DynamicLightsTickDelays> STR2OBJ = new HashMap<>() {{
        for (DynamicLightsTickDelays value : DynamicLightsTickDelays.values()) {
            put(value.toString(), value);
        }
    }};
    public final int SKIP_EVERY;

    DynamicLightsTickDelays(int skipEvery) {
        SKIP_EVERY = skipEvery;
    }

    @Override
    public String toString() {
        return this.name().toUpperCase(Locale.US);
    }

}
