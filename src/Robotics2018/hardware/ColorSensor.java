package Robotics2018.hardware;

import lejos.robotics.Color;
import Robotics2018.Config;

public class ColorSensor {
    //private static final EV3ColorSensor surfaceColorSensor = new EV3ColorSensor(Config.PORT_SENSOR_COLOR_SURFACE);

    public static int getSurfaceColor() {
        if (Config.isSimulator) {
            return Color.GREEN;

        } else {
            //TODO
            return Color.GREEN;
            //return surfaceColorSensor.getColorID();
        }

    }
}