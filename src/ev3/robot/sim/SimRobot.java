/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import common.mapping.SurfaceMap;
import ev3.navigation.NavigatorBuilder;
import ev3.robot.*;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.localization.PoseProvider;

public class SimRobot implements Robot {
    private static final String LOG_TAG = SimRobot.class.getSimpleName();

    private PoseProvider poseProvider;

    private Chassis chassis;
    private Paddle paddle;
    private Arm arm;
    private ColorSensors colorSensors;
    private Brick brick;
    private SurfaceMap surfaceMap;

    public SimRobot() {
    }

    public void setPoseProvider(PoseProvider poseProvider) {
        this.poseProvider = poseProvider;
    }

    public void setSurfaceMap(SurfaceMap surfaceMap) {
        this.surfaceMap = surfaceMap;
    }

    @Override
    public Arm getArm() {
        if (arm == null) {
            arm = new SimArm();
        }

        return arm;
    }

    @Override
    public Chassis getChassis() {
        if (chassis == null) {
            chassis = NavigatorBuilder.buildChassis(
                    new SimMotor("leftMotor"),
                    new SimMotor("rightMotor")
            );
        }

        return chassis;
    }

    @Override
    public Paddle getPaddle() {
        if (paddle == null) {
            paddle = new SimPaddle();
        }

        return paddle;
    }

    @Override
    public ColorSensors getColorSensors() {
        if (colorSensors == null) {
            if (poseProvider == null) {
                return null;
            }

            colorSensors = new SimColorSensors(poseProvider, surfaceMap);
        }

        return colorSensors;
    }

    @Override
    public Brick getBrick() {
        if (brick == null) {
            brick = new SimBrick();
        }

        return brick;
    }
}
