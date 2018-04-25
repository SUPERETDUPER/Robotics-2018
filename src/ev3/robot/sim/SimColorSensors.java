/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import datagenerator.SurfaceMapReading;
import ev3.navigation.Offset;
import ev3.robot.Robot;
import lejos.robotics.Color;
import lejos.robotics.geometry.Point;
import lejos.robotics.localization.PoseProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Simulates the color sensors by return the color the map shows for the current location
 */
class SimColorSensors implements Robot.ColorSensors {
    @NotNull
    private final PoseProvider poseProvider;
    @NotNull
    private final SurfaceMapReading surfaceMap;

    SimColorSensors(@NotNull PoseProvider poseProvider, @NotNull SurfaceMapReading surfaceMap) {
        this.poseProvider = poseProvider;
        this.surfaceMap = surfaceMap;
    }

    @Override
    public void setup() {
    }

    @Override
    public boolean isSetup() {
        return true;
    }

    @Override
    public float getColorSurfaceLeft() {
        Point currentPoint = Offset.LEFT_COLOR_SENSOR.offset(poseProvider.getPose());
        if (surfaceMap.contains(currentPoint)) {
            return surfaceMap.getColorAtPoint(currentPoint);
        } else {
            return -1;
        }
    }

    @Override
    public float getColorSurfaceRight() {
        Point currentPoint = Offset.RIGHT_COLOR_SENSOR.offset(poseProvider.getPose());
        if (surfaceMap.contains(currentPoint)) {
            return surfaceMap.getColorAtPoint(currentPoint);
        } else {
            return -1;
        }
    }

    @Override
    public int getColorContainer() {
        return Color.NONE;
    }

    @Override
    public int getColorBoat() {
        return Color.NONE;
    }
}
