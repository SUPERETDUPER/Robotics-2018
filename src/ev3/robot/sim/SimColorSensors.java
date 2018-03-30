/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import common.mapping.SurfaceMap;
import ev3.robot.ColorSensors;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

class SimColorSensors implements ColorSensors {
    @NotNull
    private final PoseProvider poseProvider;

    SimColorSensors(@NotNull PoseProvider poseProvider) {
        this.poseProvider = poseProvider;
    }

    @Override
    public int getColorSurfaceLeft() {
        Pose currentPose = poseProvider.getPose();
        return SurfaceMap.getColorAtPoint((int) currentPose.getX(), (int) currentPose.getY());
    }

    @Override
    public int getColorSurfaceRight() {
        Pose currentPose = poseProvider.getPose();
        return SurfaceMap.getColorAtPoint((int) currentPose.getX(), (int) currentPose.getY());
    }

    @Override
    public int getColorContainer() {
        Pose currentPose = poseProvider.getPose();
        return SurfaceMap.getColorAtPoint((int) currentPose.getX(), (int) currentPose.getY());
    }

    @Override
    public int getColorBoat() {
        Pose currentPose = poseProvider.getPose();
        return SurfaceMap.getColorAtPoint((int) currentPose.getX(), (int) currentPose.getY());
    }
}
