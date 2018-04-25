/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.mapping.SurfaceMapReading;
import ev3.navigation.Offset;
import ev3.navigation.Readings;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

/**
 * The probability of getting this color reading from a certain pose is calculated as being :
 * The percentage of colors matching the reading in the poses region.
 * TODO Optimize checking for all pixels in area is inefficient
 */
public class SurfaceReadings implements Readings {
    private final SurfaceMapReading surfaceMap;
    private final float colorToMatch;
    private final Offset offset;

    SurfaceReadings(SurfaceMapReading surfaceMap, float color, Offset offset) {
        this.surfaceMap = surfaceMap;
        this.colorToMatch = color;
        this.offset = offset;
    }

    public float calculateWeight(@NotNull Pose pose) {
        if (!surfaceMap.contains(pose.getLocation())) return 0;

        Point location = offset.offset(pose);

        if (!surfaceMap.contains(location)) return 0;

        if (colorToMatch == -1) return 0;

        return Util.bellCurveFunction(colorToMatch - surfaceMap.getColorAtPoint(location));
    }

    @NotNull
    @Override
    public String toString() {
        return "Surface color is " + colorToMatch;
    }
}