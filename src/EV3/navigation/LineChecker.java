package EV3.navigation;

import EV3.Controller;
import EV3.hardware.ColorSensor;

public class LineChecker {

    private int previousColor;

    public LineChecker() {
        super();

        this.previousColor = ColorSensor.getSurfaceColor();
    }

    public synchronized void check() {
        int currentColor = ColorSensor.getSurfaceColor();

        if (previousColor != currentColor) {
            Controller.get().update(new SurfaceReadings(currentColor));
            previousColor = currentColor;
        }
    }
}
