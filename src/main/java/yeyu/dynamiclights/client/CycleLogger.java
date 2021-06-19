package yeyu.dynamiclights.client;

public class CycleLogger {
    public static final int CYCLE_EVERY = 50;
    private static int CYCLE = 0;

    public static void cycle() {
        CYCLE = ++CYCLE % CYCLE_EVERY;
    }

    public static void doLog(Runnable fn) {
        if (CYCLE == 0) fn.run();
    }
}
