/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.logger.Logger;
import ev3.robot.EV3Robot;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

class Controller {
    private static final String LOG_TAG = Controller.class.getSimpleName();

    private static final int LINE_SPEED = 400;
    private static final int CORRECTION_CONSTANT_LINE_FOLLOWER = 200;
    private static final double BLACK_LINE_THRESHOLD = 0.4;
    private static final int ANGLE_90_TURN = 285;
    private static final int JUMP_START_ANGLE = 250;
    private static final int ANGLE_TO_CROSS_LINE = 90;
    private static final int DELAY_FOR_MOTOR = 100;

    private final EV3Robot robot;

    Controller(EV3Robot robot) {
        this.robot = robot;
        robot.getLeftMotor().synchronizeWith(new RegulatedMotor[]{robot.getRightMotor()});
    }

    void followLine(boolean waitForRight, int times) {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().setSpeed(LINE_SPEED);
        robot.getRightMotor().setSpeed(LINE_SPEED);
        robot.getLeftMotor().forward();
        robot.getRightMotor().forward();
        robot.getLeftMotor().startSynchronization();

        Delay.msDelay(DELAY_FOR_MOTOR);

        for (int i = 0; i < times; i++) {
            if (waitForRight) {
                followLineWaitRight();
            } else {
                followLineWaitLeft();
            }

            if (i + 1 != times) {
                int tachoCount = robot.getLeftMotor().getTachoCount();

                //Forces the robot to cross the line
                while (robot.getLeftMotor().getTachoCount() - tachoCount < ANGLE_TO_CROSS_LINE) Thread.yield();
            }

            Logger.debug(LOG_TAG, "Crossed");
        }

        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().stop();
        robot.getRightMotor().stop();
        robot.getLeftMotor().startSynchronization();
    }

    private void followLineWaitRight() {
        while (robot.getColorSensors().getColorSurfaceRight() > BLACK_LINE_THRESHOLD) {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSensors().getColorSurfaceLeft()));

            robot.getLeftMotor().startSynchronization();
            robot.getLeftMotor().setSpeed(LINE_SPEED + error);
            robot.getRightMotor().setSpeed(LINE_SPEED - error);
            robot.getLeftMotor().endSynchronization();

            Delay.msDelay(DELAY_FOR_MOTOR);
        }
    }

    private void followLineWaitLeft() {
        while (robot.getColorSensors().getColorSurfaceLeft() > BLACK_LINE_THRESHOLD) {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSensors().getColorSurfaceRight()));

            robot.getLeftMotor().startSynchronization();
            robot.getLeftMotor().setSpeed(LINE_SPEED - error);
            robot.getRightMotor().setSpeed(LINE_SPEED + error);
            robot.getLeftMotor().endSynchronization();

            Delay.msDelay(DELAY_FOR_MOTOR);
        }
    }

    void turn90(boolean turnRight){
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().setSpeed(LINE_SPEED);
        robot.getRightMotor().setSpeed(LINE_SPEED);
        if (turnRight) {
            robot.getLeftMotor().rotate(ANGLE_90_TURN);
            robot.getRightMotor().rotate(-ANGLE_90_TURN);
        } else {
            robot.getLeftMotor().rotate(-ANGLE_90_TURN);
            robot.getRightMotor().rotate(ANGLE_90_TURN);
        }
        robot.getLeftMotor().endSynchronization();
    }

    void jumpStart(){
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().setSpeed(LINE_SPEED);
        robot.getRightMotor().setSpeed(LINE_SPEED);
        robot.getLeftMotor().rotate(JUMP_START_ANGLE);
        robot.getRightMotor().rotate(JUMP_START_ANGLE);
        robot.getLeftMotor().endSynchronization();
    }

    void waitForComplete(){
        robot.getLeftMotor().waitComplete();
        robot.getRightMotor().waitComplete();
    }
}
