package Common.navigation.MCL;

import Common.mapping.SurfaceMap;
import EV3.hardware.ColorSensor;
import com.sun.istack.internal.NotNull;
import lejos.robotics.navigation.Pose;

public class SurfaceReading implements Reading {

    private final int color;

    SurfaceReading() {
        this.color = ColorSensor.getSurfaceColor();
    }

    @NotNull
    public float calculateWeight(@NotNull Pose pose) {
        if (SurfaceMap.get().contains(pose.getLocation()) && SurfaceMap.get().colorAtPoint(pose.getLocation()) == color) {
            return 1;
        }
        return 0;
    }
}