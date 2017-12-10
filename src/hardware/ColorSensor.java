package hardware;

import lejos.robotics.Color;

public class ColorSensor {
    //private static final EV3ColorSensor surfaceColorSensor = new EV3ColorSensor(Config.PORT_SENSOR_COLOR_SURFACE);

    public static int getSurfaceColor() {
        return Color.GREEN;
        //return surfaceColorSensor.getColorID();
    }
}