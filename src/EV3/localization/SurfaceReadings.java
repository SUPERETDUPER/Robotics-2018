/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.localization;

import Common.mapping.RegionSurfaceMap;
import EV3.navigation.Readings;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

public class SurfaceReadings implements Readings {

    private final int color;

    public SurfaceReadings(int color) {
        this.color = color;
    }

    public float calculateWeight(@NotNull Pose pose) {
        if (RegionSurfaceMap.get().contains(pose.getLocation()) && RegionSurfaceMap.get().getColorAtPoint(pose.getLocation()) == color) {
            return 1;
        }
        return 0;
    }

    @NotNull
    @Override
    public String toString() {
        return "Surface color is " + color;
    }
}