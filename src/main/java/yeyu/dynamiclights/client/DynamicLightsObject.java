package yeyu.dynamiclights.client;

import java.util.Objects;

public class DynamicLightsObject {
    private double lightBefore;
    private double lightCurrent;

    private boolean shouldKeepLit;


    public DynamicLightsObject(double lightBefore, double lightCurrent) {
        this.lightBefore = lightBefore;
        this.lightCurrent = lightCurrent;
        this.shouldKeepLit = false;
    }

    public DynamicLightsObject(double lightCurrent) {
        this(0, lightCurrent);
    }

    public void keepLit(double currentLight) {
        this.lightBefore = this.lightCurrent;
        this.lightCurrent = currentLight;
        this.shouldKeepLit = true;
    }

    public boolean shouldKeepLit() {
        return this.shouldKeepLit;
    }

    public double value() {
        return this.lightCurrent;
    }

    public void ack() {
        this.shouldKeepLit = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DynamicLightsObject) obj;
        return Double.doubleToLongBits(this.lightBefore) == Double.doubleToLongBits(that.lightBefore) &&
                Double.doubleToLongBits(this.lightCurrent) == Double.doubleToLongBits(that.lightCurrent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lightBefore, lightCurrent);
    }

    @Override
    public String toString() {
        return "DynamicLightObject[" +
                "lightBefore=" + lightBefore + ", " +
                "lightAfter=" + lightCurrent + ']';
    }


}
