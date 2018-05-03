/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.logger.Logger;
import ev3.robot.EV3Robot;
import lejos.utility.Delay;

public class LineFollower {
    private static final int CORRECTION_CONSTANT_LINE_FOLLOWER = 300;
    private static final float BLACK_LINE_THRESHOLD = 0.4F;
    private static final int DELAY_FOR_MOTOR_LINE_FOLLOWER = 100;
    private static final int SPEED = 400;
    private static final float CENTER = 0.5F;

    private final MotorController motorController;
    private final EV3Robot robot;

    private boolean isActive = false;
    private boolean followMiddle = false;
    private boolean followWithLeft = true;
    private int linesLeftToCross;
    private int timeAfterLastLine = 0;

    public LineFollower(MotorController motorController, EV3Robot robot) {
        this.motorController = motorController;
        this.robot = robot;
    }

    private class LineFollowerThread extends Thread {
        long timeToWaitBeforeCheckingCross = System.currentTimeMillis();
        long timeToWait = System.currentTimeMillis();

        @Override
        public void run() {
            while (true) {
                if (isActive) {
                    motorController.forward();

                    while (isActive) {
                        // Error is negative when on white and positive on black
                        int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (CENTER - getLineColor()));

                        if (shouldInverseError()) error *= -1;

                        motorController.setSpeed(SPEED + error, SPEED - error);

                        if (getCheckerColor() < BLACK_LINE_THRESHOLD && System.currentTimeMillis() > timeToWaitBeforeCheckingCross) {
                            linesLeftToCross--;
                            timeToWaitBeforeCheckingCross = System.currentTimeMillis() + DELAY_FOR_MOTOR_LINE_FOLLOWER;
                        }

                        if (linesLeftToCross == 0) break;
                    }

                    motorController.stop();
                }
            }
        }

        /**
         * Middle and Left = true
         * Middle and !left = false
         * !middle and left = false
         * !middle and !left = true
         *
         * @return if false black turns right white turns left. If true black turns left white turns right
         */
        private boolean shouldInverseError() {
            return followMiddle == followWithLeft;
        }

        private float getLineColor() {
            return followWithLeft ? robot.getColorSurfaceLeft() : robot.getColorSurfaceRight();
        }

        private float getCheckerColor() {
            return followWithLeft ? robot.getColorSurfaceRight() : robot.getColorSurfaceLeft();
        }
    }
}
