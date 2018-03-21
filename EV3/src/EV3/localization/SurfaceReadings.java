/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.localization;

import Common.mapping.SurfaceMap;
import EV3.navigation.Readings;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

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

        for (int x = (int) location.x - RADIUS ; x <= location.x + RADIUS ; x++){
            for (int y = (int) location.y - RADIUS ; y <= location.y + RADIUS ; y++){
                if (location.distance(x,y) < RADIUS){ //If (x,y) within circle
                    totalPixels ++;

                    if (SurfaceMap.getColorAtPoint(new Point(x,y)) == color){
                        matchingPixels ++;
                    }
                }
            }
        }

        return (float) matchingPixels / totalPixels;
    }

    @NotNull
    @Override
    public String toString() {
        return "Surface color is " + color;
    }
}