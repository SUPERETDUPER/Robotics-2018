package hardware;

import lejos.hardware.sensor.EV3ColorSensor;
import utils.Config;

public class ColorSensor {
    private static final EV3ColorSensor surfaceColorSensor = new EV3ColorSensor(Config.PORT_SENSOR_COLOR_SURFACE);

    public static int getSurfaceColor() {
        return surfaceColorSensor.getColorID();
    }
}