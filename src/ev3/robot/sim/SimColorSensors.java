/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import common.mapping.SurfaceMap;
import ev3.navigation.Offset;
import ev3.robot.ColorSensors;
import lejos.robotics.geometry.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

class SimColorSensors implements ColorSensors {
    @NotNull
    private final PoseProvider poseProvider;
    private final SurfaceMap surfaceMap;

    SimColorSensors(@NotNull PoseProvider poseProvider, SurfaceMap surfaceMap) {
        this.poseProvider = poseProvider;
        this.surfaceMap = surfaceMap;
    }

    @Override
    public int getColorSurfaceLeft() {
        Point currentPose = Offset.LEFT_COLOR_SENSOR.offset(poseProvider.getPose());
        return surfaceMap.getColorAtPoint((int) currentPose.x, (int) currentPose.y);
    }

    @Override
    public int getColorSurfaceRight() {
        Point currentPose = Offset.RIGHT_COLOR_SENSOR.offset(poseProvider.getPose());
        return surfaceMap.getColorAtPoint((int) currentPose.x, (int) currentPose.y);
    }

    @Override
    public int getColorContainer() {
        return -1;
    }

    @Override
    public int getColorBoat() {
        return -1;
    }
}
