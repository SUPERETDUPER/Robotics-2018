/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.mapping.SurfaceMap;
import ev3.navigation.Readings;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;

public class EdgeReadings implements Readings {
    private static final String LOG_TAG = EdgeReadings.class.getSimpleName();

    private static final int RADIUS = 10;

    private final int previousColor;
    private final int currentColor;

    public EdgeReadings(int previousColor, int currentColor) {
        this.previousColor = previousColor;
        this.currentColor = currentColor;
    }

    @Override
    public float calculateWeight(Pose pose) {
        Point location = pose.getLocation();

        float totalPixels = 0;
        float previousColorPixels = 0;
        float currentColorPixels = 0;

        for (int x = (int) location.x - RADIUS; x <= location.x + RADIUS; x++) {
            for (int y = (int) location.y - RADIUS; y <= location.y + RADIUS; y++) {
                if (location.distance(x, y) < RADIUS) { //If (x,y) within circle
                    totalPixels++;

                    int colorAtPoint = SurfaceMap.getColorAtPoint(x, y);

                    if (colorAtPoint == previousColor) {
                        previousColorPixels++;
                    } else if (colorAtPoint == currentColor) {
                        currentColorPixels++;
                    }
                }
            }
        }

        float errorOfPrevious = Math.abs(0.5F - (previousColorPixels / totalPixels));
        float errorOfCurrent = Math.abs(0.5F - (currentColorPixels / totalPixels));
        float weight = 1 - (errorOfPrevious + errorOfCurrent);

//        Logger.info(LOG_TAG, "tot : " + weight + " prev : " + errorOfPrevious + ".  cur : " + errorOfCurrent);
        return weight;
    }
}
