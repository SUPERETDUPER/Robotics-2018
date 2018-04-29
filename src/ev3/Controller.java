/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import ev3.robot.EV3Robot;
import lejos.robotics.RegulatedMotor;

class Controller {
    private static final int LINE_SPEED = 200;
    private static final int CORRECTION_CONSTANT_LINE_FOLLOWER = 10;
    private static final double BLACK_LINE_THRESHOLD = 0.2;

    private final EV3Robot robot;

    Controller(EV3Robot robot) {
        this.robot = robot;
        robot.getLeftMotor().synchronizeWith(new RegulatedMotor[]{robot.getRightMotor()});
    }

    void followLineLeft() {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().forward();
        robot.getRightMotor().forward();
        robot.getLeftMotor().startSynchronization();

        do {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSensors().getColorSurfaceLeft()));

            robot.getLeftMotor().startSynchronization();
            robot.getLeftMotor().setSpeed(LINE_SPEED + error);
            robot.getRightMotor().setSpeed(LINE_SPEED - error);
            robot.getLeftMotor().endSynchronization();

        } while (robot.getColorSensors().getColorSurfaceRight() > BLACK_LINE_THRESHOLD);
    }
}
