package EV3.hardware;

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
