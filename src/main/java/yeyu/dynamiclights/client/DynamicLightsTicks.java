package yeyu.dynamiclights.client;

public enum DynamicLightsTicks {
    SMOOTH(1),
    EASE(2),
    FASTER(3),
    FASTEST(4);

    public final int SKIP_EVERY;

    DynamicLightsTicks(int skipEvery) {
        SKIP_EVERY = skipEvery;
    }
}
