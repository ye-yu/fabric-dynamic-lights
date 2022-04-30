package yeyu.dynamiclights.client.options;

public enum DynamicLightsTickDelays {
    SMOOTH(1),
    EASE(2),
    FASTER(3),
    FASTEST(4);

    public final int SKIP_EVERY;

    DynamicLightsTickDelays(int skipEvery) {
        SKIP_EVERY = skipEvery;
    }
}
