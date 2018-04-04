/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.mapping.SurfaceMap;
import ev3.navigation.Offset;
import ev3.navigation.Readings;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

/**
 * The probability of getting this color reading from a certain pose is calculated as being :
 * The percentage of colors matching the reading in the poses region.
 */
public class SurfaceReadings implements Readings {
    private static final int RADIUS = 10;

    private final SurfaceMap surfaceMap;
    private final int color;
    private final Offset offset;

    SurfaceReadings(SurfaceMap surfaceMap, int color, Offset offset) {
        this.surfaceMap = surfaceMap;
        this.color = color;
        this.offset = offset;
    }

    public float calculateWeight(@NotNull Pose pose) {
        Point location = offset.offset(pose);

        int totalPixels = 0;
        int matchingPixels = 0;

        //Loop through every pixel in the circle
        for (int x = (int) location.x - RADIUS; x <= location.x + RADIUS; x++) {
            for (int y = (int) location.y - RADIUS; y <= location.y + RADIUS; y++) {
                if (location.distance(x, y) < RADIUS && surfaceMap.contains(x, y)) { //If (x,y) within circle
                    totalPixels++;

                    if (surfaceMap.getColorAtPoint(x, y) == color) matchingPixels++;
                }
            }
        }

        if (totalPixels == 0) return 0;


        return (float) matchingPixels / totalPixels; //Float cast required to getNavigator decimal percentage
    }

    @NotNull
    @Override
    public String toString() {
        return "Surface color is " + color;
    }
}