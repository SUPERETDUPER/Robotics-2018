/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import common.logger.Logger;
import ev3.localization.RobotPoseProvider;
import ev3.robot.*;
import lejos.robotics.chassis.Chassis;

import java.io.IOException;

public class SimRobot implements Robot {
    private static final String LOG_TAG = SimRobot.class.getSimpleName();

    private static final double WHEEL_OFFSET = 81.5;
    private static final double WHEEL_DIAMETER = 55.9;

    private final RobotPoseProvider robotPoseProvider;

    public SimRobot(RobotPoseProvider robotPoseProvider) {
        this.robotPoseProvider = robotPoseProvider;
    }

    @Override
    public Arm getArm() {
        return new SimArm();
    }

    @Override
    public Chassis getChassis() {
        return Util.buildChassis(new SimMotor("leftMotor"), new SimMotor("rightMotor"), WHEEL_DIAMETER, WHEEL_OFFSET);
    }

    @Override
    public Paddle getPaddle() {
        return new SimPaddle();
    }

    @Override
    public ColorSensors getColorSensors() {
        return new SimColorSensors(robotPoseProvider);
    }

    @Override
    public Brick getBrick() {
        return new SimBrick();
    }

    private class SimBrick implements Brick {
        @Override
        public void waitForUserConfirmation() {
            try {
                System.out.println("Press enter to continue");
                //noinspection ResultOfMethodCallIgnored
                System.in.read();
            } catch (IOException e) {
                Logger.error(LOG_TAG, e.toString());
            }
        }
    }

}
