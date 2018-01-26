package EV3.hardware;

import Common.Config;
import lejos.robotics.Color;

public class ColorSensor {
    //private static final EV3ColorSensor surfaceColorSensor = new EV3ColorSensor(Config.PORT_SENSOR_COLOR_SURFACE);

    public static int getSurfaceColor() {
        if (Config.useSimulator) {
            return Color.WHITE;

        } else {
            //TODO
            return Color.WHITE;
            //return surfaceColorSensor.getColorID();
        }

    }
}