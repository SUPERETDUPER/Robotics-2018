/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.Config;
import common.mapping.SurfaceMap;
import ev3.communication.ComManager;
import ev3.localization.RobotPoseProvider;
import ev3.robot.ColorSensors;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.MoveController;
import lejos.robotics.navigation.Pose;

public class NavigatorBuilder {

    private static final double ANGULAR_ACCELERATION = 200;
    private static final double LINEAR_ACCELERATION = 400;
    private static final double ANGULAR_SPEED_PERCENT = 0.5;
    private static final double LINEAR_SPEED_PERCENT = 0.5;

    private static final Pose STARTING_POSE = new Pose(2152, 573, 180);

    public static MoveController getMoveProvider(Chassis chassis){
        MyMovePilot pilot = new MyMovePilot(chassis);

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);
        pilot.setLinearSpeed(pilot.getMaxLinearSpeed() * LINEAR_SPEED_PERCENT);
        pilot.setAngularSpeed(pilot.getMaxAngularSpeed() * ANGULAR_SPEED_PERCENT);

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
