package hardware;

import PC.Connection;
import lejos.robotics.Color;

public class ColorSensor {
    //private static final EV3ColorSensor surfaceColorSensor = new EV3ColorSensor(Config.PORT_SENSOR_COLOR_SURFACE);

    public static int getSurfaceColor() {
        if (Connection.runningOn == Connection.RUNNING_ON.EV3) {
            //Todo :
            return Color.GREEN;
            //return surfaceColorSensor.getColorID();
        } else {
            return Color.GREEN;
        }

    }
}