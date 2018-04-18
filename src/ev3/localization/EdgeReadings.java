/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.mapping.SurfaceMap;
import ev3.navigation.Offset;
import ev3.navigation.Readings;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;

class EdgeReadings implements Readings {
    private static final int RADIUS = 20;

    private final int previousColor;
    private final int currentColor;

    private final SurfaceMap surfaceMap;

    private final Offset offset;

    EdgeReadings(SurfaceMap surfaceMap, int previousColor, int currentColor, Offset offset) {
        this.previousColor = previousColor;
        this.currentColor = currentColor;
        this.surfaceMap = surfaceMap;
        this.offset = offset;
    }

    public float calculateWeight(Pose pose) {
        if (!surfaceMap.contains(pose.getLocation())) return 0;

        Point location = offset.offset(pose);

        if (!surfaceMap.contains(location)) return 0;

        float totalPixels = 0;
        float previousColorPixels = 0;
        float currentColorPixels = 0;

        for (int x = (int) location.x - RADIUS; x <= location.x + RADIUS; x++) {
            for (int y = (int) location.y - RADIUS; y <= location.y + RADIUS; y++) {
                if (location.distance(x, y) < RADIUS && surfaceMap.contains(new Point(x,y))) { //If (x,y) within circle
                    totalPixels++;

                    int colorAtPoint = surfaceMap.getColorAtPoint(new Point(x,y));

                    if (colorAtPoint == previousColor) {
                        previousColorPixels++;
                    } else if (colorAtPoint == currentColor) {
                        currentColorPixels++;
                    }
                }
            }
        }

        if (totalPixels == 0) return 0;

        float errorOfPrevious = Math.abs(0.5F - (previousColorPixels / totalPixels));
        float errorOfCurrent = Math.abs(0.5F - (currentColorPixels / totalPixels));

//        Logger.info(LOG_TAG, "tot : " + weight + " prev : " + errorOfPrevious + ".  cur : " + errorOfCurrent);
        return 1 - (errorOfPrevious + errorOfCurrent);
    }
}
