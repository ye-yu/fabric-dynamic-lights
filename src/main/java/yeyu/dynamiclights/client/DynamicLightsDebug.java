package yeyu.dynamiclights.client;

import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class DynamicLightsDebug {
    private static final Logger LOGGER = LogManager.getLogger();
    private final double[] previous;

    public DynamicLightsDebug(final int capacity) {
        this.previous = new double[capacity];
        for (int i = 0; i < capacity; i++) {
            this.previous[i] = 0;
        }
    }

    public void change(double... vars) {
        final int iterLength = Math.min(vars.length, this.previous.length);
        boolean shouldLog = false;
        for (int i = 0; i < iterLength; i++) {
            if (MathHelper.approximatelyEquals(this.previous[i], vars[i])) continue;
            shouldLog = true;
            this.previous[i] = vars[i];
        }

        if (shouldLog) {
            LOGGER.info("Values changed! {}", Arrays.toString(this.previous));
        }
    }

}
