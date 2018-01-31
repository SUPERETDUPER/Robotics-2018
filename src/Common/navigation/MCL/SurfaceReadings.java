package Common.navigation.MCL;

import Common.mapping.SurfaceMap;
import EV3.hardware.ColorSensor;
import com.sun.istack.internal.NotNull;
import lejos.robotics.navigation.Pose;

public class SurfaceReadings implements Readings {

    private final int color;

    SurfaceReadings() {
        this.color = ColorSensor.getSurfaceColor();
    }

    @NotNull
    public float calculateWeight(@NotNull Pose pose) {
        if (SurfaceMap.contains(pose.getLocation()) && SurfaceMap.colorAtPoint(pose.getLocation()) == color) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Surface color " + color;
    }
}