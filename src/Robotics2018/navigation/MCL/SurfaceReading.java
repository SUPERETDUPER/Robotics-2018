package Robotics2018.navigation.MCL;

import com.sun.istack.internal.NotNull;
import Robotics2018.mapping.SurfaceMap;
import Robotics2018.hardware.ColorSensor;

public class SurfaceReading implements Reading {

    private final int color;

    SurfaceReading() {
        this.color = ColorSensor.getSurfaceColor();
    }

    @NotNull
    public float calculateWeight(@NotNull Particle particle) {
        if (SurfaceMap.get().contains(particle.getPose().getLocation()) && SurfaceMap.get().colorAtPoint(particle.getLocation()) == color) {
            return 1;
        }
        return 0;
    }
}