package yeyu.dynamiclights.client;

public enum DynamicLightsTicks {
    SMOOTH(1),
    EASE(2),
    FASTER(4),
    FASTEST(7);

    public final int SKIP_EVERY;

    DynamicLightsTicks(int skipEvery) {
        SKIP_EVERY = skipEvery;
    }
}
