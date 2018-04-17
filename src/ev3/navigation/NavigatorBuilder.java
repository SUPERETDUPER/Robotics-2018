/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.mapping.SurfaceMap;
import ev3.communication.ComManager;
import ev3.localization.RobotPoseProvider;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

/**
 * Static class that is used to build the different the objects leading up to the Navigator
 */
public class NavigatorBuilder {

    private static final double ANGULAR_ACCELERATION = 90;
    private static final double LINEAR_ACCELERATION = 150;
    private static final double ANGULAR_SPEED = 100;
    private static final double LINEAR_SPEED = 150;

    private static final Pose STARTING_POSE = new Pose(2152, 573, 180);

    private static final double WHEEL_OFFSET = 65; //Real value is around 56 but testing shows higher is better
    private static final double WHEEL_DIAMETER = 81.6;

    public static MyMovePilot buildMoveProvider(Chassis chassis) {
        MyMovePilot pilot = new MyMovePilot(chassis);
        pilot.setMinRadius(WHEEL_OFFSET);

        return pilot;
    }

    public static PoseProvider buildPoseProvider(SurfaceMap surfaceMap, MyMovePilot pilot) {
        RobotPoseProvider poseProvider = new RobotPoseProvider(surfaceMap, pilot, STARTING_POSE);

        if (ComManager.getDataListener() != null) {
            ComManager.getDataListener().attachToRobotPoseProvider(poseProvider);
        }

//        PoseProvider poseProvider = pilot.getChassis().getPoseProvider();
//        poseProvider.setPose(STARTING_POSE);

        return poseProvider;
    }

    @NotNull
    public static Chassis buildChassis(RegulatedMotor leftMotor, RegulatedMotor rightMotor) {
        Wheel[] wheels = new Wheel[]{
                WheeledChassis.modelWheel(leftMotor, WHEEL_DIAMETER).offset(WHEEL_OFFSET),
                WheeledChassis.modelWheel(rightMotor, WHEEL_DIAMETER).offset(-WHEEL_OFFSET)
        };

        Chassis chassis = new WheeledChassis(wheels, WheeledChassis.TYPE_DIFFERENTIAL);

        chassis.setAngularAcceleration(ANGULAR_ACCELERATION);
        chassis.setLinearAcceleration(LINEAR_ACCELERATION);
        chassis.setLinearSpeed(LINEAR_SPEED);
        chassis.setAngularSpeed(ANGULAR_SPEED);

        return chassis;
    }
}