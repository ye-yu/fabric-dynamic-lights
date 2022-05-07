package yeyu.dynamiclights.client.options;

import java.util.HashMap;
import java.util.Locale;

public enum DynamicLightsPrecision {
    MINIMAL,
    ENHANCED,
    POWERFUL,
    ;

    public static final HashMap<String, DynamicLightsPrecision> STR2OBJ = new HashMap<>() {{
        for (DynamicLightsPrecision value : DynamicLightsPrecision.values()) {
            put(value.toString(), value);
        }
    }};

    @Override
    public String toString() {
        return this.name().toUpperCase(Locale.US);
    }
}
