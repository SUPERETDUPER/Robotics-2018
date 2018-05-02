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

    private static final int SPEED = 400;
    private static final int CORRECTION_CONSTANT_LINE_FOLLOWER = 200;
    private static final float BLACK_LINE_THRESHOLD = 0.4F;
    private static final int ANGLE_TO_TURN_90 = 285;
    private static final int DISTANCE_TO_CLEAR_STARTING_AREA = 250;
    private static final int DISTANCE_TO_CROSS_LINE = 90;
    private static final int DELAY_FOR_MOTOR_LINE_FOLLOWER = 100;
    private static final int BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER = 270;
    private static final int DISTANCE_TEMP_REG_FROM_LINE = 120;

    private final EV3Robot robot;

    Controller(EV3Robot robot) {
        this.robot = robot;
        robot.getLeftMotor().synchronizeWith(new RegulatedMotor[]{robot.getRightMotor()});
    }

    void turn90(boolean turnRight){
        turn90(turnRight, false);
    }

    void turn90(boolean turnRight, boolean immediateReturn) {
        if (turnRight) {
            rotate(ANGLE_TO_TURN_90, -ANGLE_TO_TURN_90, immediateReturn);
        } else {
            rotate(-ANGLE_TO_TURN_90, ANGLE_TO_TURN_90, immediateReturn);
        }
    }

    void jumpStart() {
        move(DISTANCE_TO_CLEAR_STARTING_AREA, false);
    }

    void goToTempReg(boolean isOnRightSide, boolean isInFront) {
        move(isInFront ? BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER : -BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER);

        turn90(isOnRightSide, false);

        move(DISTANCE_TEMP_REG_FROM_LINE);
    }

    void goBackTempReg(boolean isOnRightSide, boolean isInFront) {
        move(-DISTANCE_TEMP_REG_FROM_LINE);

        turn90(!isOnRightSide, false);

        move(isInFront ? -BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER : BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER);
    }

    void followLine(boolean waitForRight, int times) {
        forward();

        Delay.msDelay(DELAY_FOR_MOTOR_LINE_FOLLOWER);

        for (int i = 0; i < times; i++) {
            if (waitForRight) {
                followLineWaitRight();
            } else {
                followLineWaitLeft();
            }

            //If not on last time travel for a bit further to cross line
            if (i < times - 1) {
                //TODO Check if can be replaced with travel
                int tachoCount = robot.getLeftMotor().getTachoCount();

                //Forces the robot to cross the line
                while (robot.getLeftMotor().getTachoCount() - tachoCount < DISTANCE_TO_CROSS_LINE) Thread.yield();
            }

            Logger.debug(LOG_TAG, "Line crossed : " + i);
        }

        stop();
    }

    public void waitComplete() {
        robot.getLeftMotor().waitComplete();
        robot.getRightMotor().waitComplete();
    }

    //MOTOR HELPER METHODS

    private void followLineWaitRight() {
        while (robot.getColorSurfaceRight() > BLACK_LINE_THRESHOLD) {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSurfaceLeft()));

            setSpeed(SPEED + error, SPEED - error);

            Delay.msDelay(DELAY_FOR_MOTOR_LINE_FOLLOWER);
        }
    }

    private void followLineWaitLeft() {
        while (robot.getColorSurfaceLeft() > BLACK_LINE_THRESHOLD) {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSurfaceRight()));

            setSpeed(SPEED - error, SPEED + error);

            Delay.msDelay(DELAY_FOR_MOTOR_LINE_FOLLOWER);
        }
    }

    private void move(int amount) {
        rotate(amount, amount);
    }

    private void move(int amount, boolean immediateReturn) {
        rotate(amount, amount, immediateReturn);
    }

    private void rotate(int left, int right) {
        rotate(left, right, false);
    }

    private void rotate(int left, int right, boolean immediateReturn) {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().setSpeed(SPEED);
        robot.getRightMotor().setSpeed(SPEED);
        robot.getLeftMotor().rotate(left);
        robot.getRightMotor().rotate(right);
        robot.getLeftMotor().endSynchronization();
        Logger.debug(LOG_TAG, "Turning left: " + left + " right: " + right + " immediateReturn " + immediateReturn);

        if (immediateReturn) return;
        robot.getLeftMotor().waitComplete();
        robot.getRightMotor().waitComplete();
    }

    private void setSpeed(int leftSpeed, int rightSpeed) {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().setSpeed(leftSpeed);
        robot.getRightMotor().setSpeed(rightSpeed);
        robot.getLeftMotor().endSynchronization();
    }

    private void forward() {
        robot.getLeftMotor().startSynchronization();
        robot.getLeftMotor().setSpeed(SPEED);
        robot.getRightMotor().setSpeed(SPEED);
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
