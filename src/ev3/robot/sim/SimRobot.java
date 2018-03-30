/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import ev3.localization.RobotPoseProvider;
import ev3.robot.*;
import lejos.robotics.chassis.Chassis;

public class SimRobot implements Robot {
    private static final String LOG_TAG = SimRobot.class.getSimpleName();

    private static final double WHEEL_OFFSET = 81.5;
    private static final double WHEEL_DIAMETER = 55.9;

    private final RobotPoseProvider robotPoseProvider;

    private Chassis chassis;
    private Paddle paddle;
    private Arm arm;
    private ColorSensors colorSensors;
    private Brick brick;

    public SimRobot(RobotPoseProvider robotPoseProvider) {
        this.robotPoseProvider = robotPoseProvider;
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
            chassis = Util.buildChassis(new SimMotor("leftMotor"), new SimMotor("rightMotor"), WHEEL_DIAMETER, WHEEL_OFFSET);
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
            colorSensors = new SimColorSensors(robotPoseProvider);
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
