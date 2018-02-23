package EV3.navigation;

import Common.Logger;
import EV3.Controller;
import EV3.hardware.ColorSensor;

/**
 * Check method checks if the color under the robot has changed. If so it calls the pose provider update method
 */
public class LineChecker {
    private static final String LOG_TAG = LineChecker.class.getSimpleName();

    private int previousColor;

    public LineChecker() {
        super();

        this.previousColor = ColorSensor.getSurfaceColor();
    }

    public synchronized void check() {
        int currentColor = ColorSensor.getSurfaceColor();

        if (previousColor != currentColor) {
            Logger.info(LOG_TAG, "Changed zone " + previousColor + " to " + currentColor);
            Controller.get().update(new SurfaceReadings(currentColor));
            previousColor = currentColor;
        }
    }
}
