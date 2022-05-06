package yeyu.dynamiclights.client.options;

import com.google.common.base.Suppliers;
import yeyu.dynamiclights.SerializableEnum;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;

public enum DynamicLightsPrecision implements SerializableEnum<DynamicLightsPrecision> {
    MINIMAL,
    ENHANCED,
    POWERFUL,
    ;
    public static final Supplier<HashMap<String, DynamicLightsPrecision>> STR2OBJ = Suppliers.memoize(() -> new HashMap<String, DynamicLightsPrecision>() {{
        for (DynamicLightsPrecision value : DynamicLightsPrecision.values()) {
            put(value.name().toUpperCase(Locale.US), value);
        }
    }})::get;

    @Override
    public DynamicLightsPrecision[] getValues() {
        return DynamicLightsPrecision.values();
    }
}
