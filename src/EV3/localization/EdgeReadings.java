/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.localization;

import Common.mapping.SurfaceMap;
import EV3.navigation.Readings;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;

public class EdgeReadings implements Readings {
    private static final float FACTOR = 10;

    private final int previousColor;
    private final int currentColor;

    public EdgeReadings(int previousColor, int currentColor) {
        this.previousColor = previousColor;
        this.currentColor = currentColor;
    }


    @Override
    public float calculateWeight(Pose pose) {
        Point poseLocation = pose.getLocation();

        float distanceToPrev = SurfaceMap.get().distanceToColor(poseLocation, previousColor);
        float distanceToCur = SurfaceMap.get().distanceToColor(poseLocation, currentColor);

        return FACTOR / (distanceToCur + distanceToPrev);
    }
}
