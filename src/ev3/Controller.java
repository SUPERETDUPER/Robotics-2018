/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import ev3.robot.EV3Robot;
import lejos.robotics.RegulatedMotor;

class Controller {
    private static final int LINE_SPEED = 400;
    private static final int CORRECTION_CONSTANT_LINE_FOLLOWER = 100;
    private static final double BLACK_LINE_THRESHOLD = 0.2;

    private final EV3Robot robot;

    Controller(EV3Robot robot) {
        this.robot = robot;
        robot.getLeftMotor().synchronizeWith(new RegulatedMotor[]{robot.getRightMotor()});
    }

    void followLine(boolean waitForRight, int times) {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().forward();
        robot.getRightMotor().forward();
        robot.getLeftMotor().startSynchronization();

        for (int i = 0; i < times; i++) {
            if (waitForRight) {
                followLineWaitRight();
            } else {
                followLineWaitLeft();
            }

            if (i + 1 != times) {
                int tachoCount = robot.getLeftMotor().getTachoCount();

                //Forces the robot to cross the line
                while (robot.getLeftMotor().getTachoCount() - tachoCount < 30) Thread.yield();
            }
        }

        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().stop();
        robot.getRightMotor().stop();
        robot.getLeftMotor().startSynchronization();
    }

    private void followLineWaitRight() {
        do {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSensors().getColorSurfaceLeft()));

            robot.getLeftMotor().startSynchronization();
            robot.getLeftMotor().setSpeed(LINE_SPEED + error);
            robot.getRightMotor().setSpeed(LINE_SPEED - error);
            robot.getLeftMotor().endSynchronization();

        } while (robot.getColorSensors().getColorSurfaceRight() > BLACK_LINE_THRESHOLD);
    }

    private void followLineWaitLeft() {
        do {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSensors().getColorSurfaceRight()));

            robot.getLeftMotor().startSynchronization();
            robot.getLeftMotor().setSpeed(LINE_SPEED - error);
            robot.getRightMotor().setSpeed(LINE_SPEED + error);
            robot.getLeftMotor().endSynchronization();

        } while (robot.getColorSensors().getColorSurfaceLeft() > BLACK_LINE_THRESHOLD);
    }
}
