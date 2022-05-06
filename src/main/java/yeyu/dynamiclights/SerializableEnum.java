package yeyu.dynamiclights;

public interface SerializableEnum<T extends Enum<T>> {

    T[] getValues();
    default T byId(int id) {
        final T[] values = getValues();
        return values[Math.floorMod(id, values.length)];
    }

    default int getId(T obj) {
        return obj.ordinal();
    }
}
