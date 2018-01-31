package EV3.navigation;

import EV3.hardware.ColorSensor;

class LineChecker {

    private int previousColor;

    public LineChecker() {
        super();

        this.previousColor = ColorSensor.getSurfaceColor();
    }

    public void check() {

        int currentColor = ColorSensor.getSurfaceColor();

        if (previousColor != currentColor) {
            //TODO
            previousColor = currentColor;
        }
    }
}
