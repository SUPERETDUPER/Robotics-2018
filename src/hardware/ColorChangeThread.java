package hardware;

import navigation.EdgeReading;
import navigation.MyPoseProvider;

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
            // TODO : Update pose provider
            MyPoseProvider.get().update(new EdgeReading(previousColor, currentColor));
            previousColor = currentColor;
        }
    }
}
