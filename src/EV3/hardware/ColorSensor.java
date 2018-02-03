package EV3.hardware;

import Common.Config;
import Common.mapping.SurfaceMap;
import EV3.Controller;
import lejos.hardware.sensor.EV3ColorSensor;
import org.jetbrains.annotations.Nullable;

public class ColorSensor {
    @Nullable
    private static final EV3ColorSensor surfaceColorSensor;

    static {
        if (Config.useSimulator) {
            surfaceColorSensor = null;
        } else {
            surfaceColorSensor = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE);
        }
    }

    public static int getSurfaceColor() {
        if (Config.useSimulator) {
            return SurfaceMap.get().getColorAtPoint(Controller.get().getPose().getLocation());
        } else {
            return surfaceColorSensor.getColorID();
        }
    }
}