package Robotics2018.hardware;

import Robotics2018.navigation.Controller;
import Robotics2018.navigation.MCL.EdgeReading;

class ColorChangeThread extends Thread {

    private int previousColor;

    public ColorChangeThread() {
        super();

        this.previousColor = ColorSensor.getSurfaceColor();
    }

    @Override
    public void run() {
        super.run();

        int currentColor = ColorSensor.getSurfaceColor();

        if (previousColor != currentColor) {
            Controller.get().update(new EdgeReading(previousColor, currentColor));
            previousColor = currentColor;
        }
    }
}
