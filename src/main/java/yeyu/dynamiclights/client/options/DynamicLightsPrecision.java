package yeyu.dynamiclights.client.options;

import com.google.common.base.Suppliers;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;

public enum DynamicLightsPrecision {
    MINIMAL,
    ENHANCED,
    POWERFUL,
    ;
    public static final Supplier<HashMap<String, DynamicLightsPrecision>> STR2OBJ = Suppliers.memoize(() -> new HashMap<String, DynamicLightsPrecision>() {{
        for (DynamicLightsPrecision value : DynamicLightsPrecision.values()) {
            put(value.name().toUpperCase(Locale.US), value);
        }
    }})::get;
}
