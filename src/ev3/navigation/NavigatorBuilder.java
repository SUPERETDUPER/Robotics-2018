/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.mapping.SurfaceMap;
import ev3.communication.ComManager;
import ev3.localization.RobotPoseProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.MoveController;
import lejos.robotics.navigation.Pose;

public class NavigatorBuilder {

    private static final double ANGULAR_ACCELERATION = 150;
    private static final double LINEAR_ACCELERATION = 400;
    private static final double ANGULAR_SPEED = 100;
    private static final double LINEAR_SPEED = 200;

    private static final Pose STARTING_POSE = new Pose(2152, 573, 180);

    public static ArcRotateMoveController getMoveProvider(Chassis chassis){
        MyMovePilot pilot = new MyMovePilot(chassis);

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);
        pilot.setLinearSpeed(LINEAR_SPEED);
        pilot.setAngularSpeed(ANGULAR_SPEED);

        return pilot;
    }

    public static RobotPoseProvider getPoseProvider(SurfaceMap surfaceMap, MoveController pilot){
        RobotPoseProvider poseProvider = new RobotPoseProvider(surfaceMap, pilot, STARTING_POSE);

        ComManager comManager = ComManager.get();

        if (comManager != null) {
            comManager.getDataListener().attachToRobotPoseProvider(poseProvider);
        }

        return poseProvider;
    }
}
