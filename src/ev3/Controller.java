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
    private static final int LINE_TEMP_ANGLE = 270;
    private static final int CLEAR_TEMP_ANGLE = 120;

    private final EV3Robot robot;

    Controller(EV3Robot robot) {
        this.robot = robot;
        robot.getLeftMotor().synchronizeWith(new RegulatedMotor[]{robot.getRightMotor()});
    }

    void followLine(boolean waitForRight, int times) {
        forward();

        Delay.msDelay(DELAY_FOR_MOTOR);

        for (int i = 0; i < times; i++) {
            if (waitForRight) {
                followLineWaitRight();
            } else {
                followLineWaitLeft();
            }

            //If not on last time travel for a bit further to cross line
            if (i < times - 1) {
                int tachoCount = robot.getLeftMotor().getTachoCount();

                //Forces the robot to cross the line
                while (robot.getLeftMotor().getTachoCount() - tachoCount < ANGLE_TO_CROSS_LINE) Thread.yield();
            }

            Logger.debug(LOG_TAG, "Crossed");
        }

        stop();
    }

    private void followLineWaitRight() {
        while (robot.getColorSurfaceRight() > BLACK_LINE_THRESHOLD) {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSurfaceLeft()));

            setSpeed(LINE_SPEED + error, LINE_SPEED - error);

            Delay.msDelay(DELAY_FOR_MOTOR);
        }
    }

    private void followLineWaitLeft() {
        while (robot.getColorSurfaceLeft() > BLACK_LINE_THRESHOLD) {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSurfaceRight()));

            setSpeed(LINE_SPEED - error, LINE_SPEED + error);

            Delay.msDelay(DELAY_FOR_MOTOR);
        }
    }

    void turn90(boolean turnRight, boolean immediateReturn) {
        if (turnRight) {
            rotate(ANGLE_90_TURN, -ANGLE_90_TURN);
        } else {
            rotate(-ANGLE_90_TURN, ANGLE_90_TURN);
        }

        checkWaitForComplete(immediateReturn);
    }

    void jumpStart(boolean immediateReturn) {
        move(JUMP_START_ANGLE);
    }

    void goToTempReg(boolean isOnRightSide, boolean isInFront) {
        int moveDirection;

        if (isInFront) {
            moveDirection = 1;
        } else {
            moveDirection = -1;
        }

        move(LINE_TEMP_ANGLE * moveDirection);

        turn90(isOnRightSide, false);
    }

    void returnFromTempReg(boolean isOnRightSide, boolean isInFront) {
        int moveDirection;

        if (isInFront) {
            moveDirection = -1;
        } else {
            moveDirection = 1;
        }

        turn90(!isOnRightSide, false);

        move(LINE_TEMP_ANGLE * moveDirection);
    }

    void moveTempReg(boolean isForward) {
        int moveDirection = 1;
        if (!isForward) moveDirection = -1;

        move(CLEAR_TEMP_ANGLE * moveDirection);
    }

    //MOTOR HELPER METHODS

    private void checkWaitForComplete(boolean immediateReturn) {
        if (immediateReturn) return;
        robot.getLeftMotor().waitComplete();
        robot.getRightMotor().waitComplete();
    }

    private void move(int amount, boolean immediateReturn) {
        rotate(amount, amount);
        checkWaitForComplete(immediateReturn);
    }

    private void move(int amount) {
        rotate(amount, amount);
        checkWaitForComplete(false);
    }

    private void rotate(int left, int right) {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().setSpeed(LINE_SPEED);
        robot.getRightMotor().setSpeed(LINE_SPEED);
        robot.getLeftMotor().rotate(left);
        robot.getRightMotor().rotate(right);
        robot.getLeftMotor().endSynchronization();
    }

    private void setSpeed(int leftSpeed, int rightSpeed) {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().setSpeed(leftSpeed);
        robot.getRightMotor().setSpeed(rightSpeed);
        robot.getLeftMotor().endSynchronization();
    }

    private void forward() {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().setSpeed(LINE_SPEED);
        robot.getRightMotor().setSpeed(LINE_SPEED);
        robot.getLeftMotor().forward();
        robot.getRightMotor().forward();
        robot.getLeftMotor().startSynchronization();
    }

    private void stop() {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().stop();
        robot.getRightMotor().stop();
        robot.getLeftMotor().startSynchronization();
    }
}
