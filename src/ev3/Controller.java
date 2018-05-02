/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.logger.Logger;
import ev3.robot.EV3Robot;
import lejos.utility.Delay;

import java.awt.*;

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

    private final EV3Chassis chassis;
    private final EV3Robot robot;

    Controller(EV3Robot robot) {
        this.robot = robot;
        this.chassis = new EV3Chassis(robot.getLeftMotor(), robot.getRightMotor(), SPEED);
    }

    void turn90(boolean turnRight) {
        turn90(turnRight, false);
    }

    void turn90(boolean turnRight, boolean immediateReturn) {
        if (turnRight) {
            chassis.rotate(ANGLE_TO_TURN_90, -ANGLE_TO_TURN_90, immediateReturn);
        } else {
            chassis.rotate(-ANGLE_TO_TURN_90, ANGLE_TO_TURN_90, immediateReturn);
        }
    }

    void jumpStart() {
        chassis.move(DISTANCE_TO_CLEAR_STARTING_AREA, false);
    }

    void goToTempReg(boolean isOnRightSide, boolean isInFront) {
        chassis.move(isInFront ? BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER : -BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER);

        turn90(isOnRightSide, false);

        chassis.move(DISTANCE_TEMP_REG_FROM_LINE);
    }

    void goBackTempReg(boolean isOnRightSide, boolean isInFront) {
        chassis.move(-DISTANCE_TEMP_REG_FROM_LINE);

        turn90(!isOnRightSide, false);

        chassis.move(isInFront ? -BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER : BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER);
    }

    void followLine(boolean waitForRight, int times, boolean middle) {
        chassis.forward();

        Delay.msDelay(DELAY_FOR_MOTOR_LINE_FOLLOWER);

        for (int i = 0; i < times; i++) {
            if (waitForRight) {
                followLineWaitRight(middle);
            } else {
                followLineWaitLeft(middle);
            }

            //If not on last time travel for a bit further to cross line
            if (i < times - 1) {
                chassis.move(DISTANCE_TO_CROSS_LINE);
            }

            Logger.debug(LOG_TAG, "Line crossed : " + i);
        }

        chassis.stop();
    }

    //MOTOR HELPER METHODS

    private void followLineWaitRight(boolean middle) {
        while (robot.getColorSurfaceRight() > BLACK_LINE_THRESHOLD) {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSurfaceLeft()));

            int middleSign = middle ? 1 : -1;

            chassis.setSpeed(SPEED - error * middleSign, SPEED + error * middleSign);

            Delay.msDelay(DELAY_FOR_MOTOR_LINE_FOLLOWER);
        }
    }

    private void followLineWaitLeft(boolean middle) {
        while (robot.getColorSurfaceLeft() > BLACK_LINE_THRESHOLD) {
            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (0.5F - robot.getColorSurfaceRight()));

            int middleSign = middle ? 1 : -1;

            chassis.setSpeed(SPEED + error * middleSign, SPEED - error * middleSign);

            Delay.msDelay(DELAY_FOR_MOTOR_LINE_FOLLOWER);
        }
    }


}
