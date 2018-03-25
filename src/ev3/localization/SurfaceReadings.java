/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.mapping.SurfaceMap;
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

    private final int color;

    public SurfaceReadings(int color) {
        this.color = color;
    }

    public float calculateWeight(@NotNull Pose pose) {
        Point location = pose.getLocation();

        int totalPixels = 0;
        int matchingPixels = 0;

        //Loop through every pixel in the circle
        for (int x = (int) location.x - RADIUS; x <= location.x + RADIUS; x++) {
            for (int y = (int) location.y - RADIUS; y <= location.y + RADIUS; y++) {
                if (location.distance(x, y) < RADIUS && SurfaceMap.contains(x, y)) { //If (x,y) within circle
                    totalPixels++;

                    if (SurfaceMap.getColorAtPoint(x, y) == color) matchingPixels++;
                }
            }
        }

        return (float) matchingPixels / totalPixels; //Float cast required to get decimal percentage
    }

    @NotNull
    @Override
    public String toString() {
        return "Surface color is " + color;
    }
}