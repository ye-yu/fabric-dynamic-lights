package yeyu.dynamiclights.client.options;

import java.util.HashMap;
import java.util.Locale;

// TODO: to be refactored or removed
public enum DynamicLightsPrecision {
    MINIMAL,
    ENHANCED,
    POWERFUL,
    ;
    @Override
    public String toString() {
        return this.name().toUpperCase(Locale.US);
    }

    public static final HashMap<String, DynamicLightsPrecision> STR2OBJ = new HashMap<>() {{
        for (DynamicLightsPrecision value : DynamicLightsPrecision.values()) {
            put(value.toString(), value);
        }
    }};
}
