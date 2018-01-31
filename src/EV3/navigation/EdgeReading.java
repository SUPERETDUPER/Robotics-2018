package EV3.navigation;

import lejos.robotics.navigation.Pose;

public class EdgeReading implements Readings {
    private final int previousColor;
    private final int currentColor;

    public EdgeReading(int previousColor, int currentColor) {
        this.previousColor = previousColor;
        this.currentColor = currentColor;
    }

    @Override
    public float calculateWeight(Pose pose) {
        //TODO :
        return 0;
    }
}
