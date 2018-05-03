/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.logger.Logger;
import ev3.robot.EV3Robot;
import lejos.utility.Delay;

public class LineFollower {
    private static final String LOG_TAG = LineFollower.class.getSimpleName();

    private final MotorController motorController;
    private final EV3Robot robot;

    /**
     * Whether the line follower is following a line
     */
    private boolean isActive = false;

    private boolean followMiddle;
    private boolean followWithLeft;
    private int linesLeftToCross;
    private int timeAfterLastLine;

    public LineFollower(MotorController motorController, EV3Robot robot) {
        this.motorController = motorController;
        this.robot = robot;

        new LineFollowerThread().start();
    }

    public void startLineFollower(boolean followWithLeft, boolean followMiddle, int linesToCross, int timeAfterLastLine, boolean immediateReturn) {
        this.followMiddle = followMiddle;
        this.followWithLeft = followWithLeft;
        this.linesLeftToCross = linesToCross;
        this.timeAfterLastLine = timeAfterLastLine;
        isActive = true;

        if (!immediateReturn) waitForComplete();
    }

    public void stopFollowing() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public void waitForComplete() {
        while (isActive) Thread.yield();
    }

    private class LineFollowerThread extends Thread {
        private static final int CORRECTION_CONSTANT_LINE_FOLLOWER = 300;
        private static final float BLACK_LINE_THRESHOLD = 0.4F;
        private static final int DELAY_FOR_MOTOR_LINE_FOLLOWER = 100;
        private static final int SPEED = 400;
        private static final float CENTER = 0.5F;

        long timeToWaitBeforeCheckingCross = 0;
        long timeToWait = 0;

        @Override
        public void run() {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    if (isActive) {
                        motorController.forward();

                        Delay.msDelay(DELAY_FOR_MOTOR_LINE_FOLLOWER);

                        while (isActive) {
                            // Error is negative when on white and positive on black
                            int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (CENTER - getLineColor()));

                            if (shouldInverseError()) error *= -1;

                            motorController.setSpeed(SPEED + error, SPEED - error);

                            if (getCheckerColor() < BLACK_LINE_THRESHOLD && System.currentTimeMillis() > timeToWaitBeforeCheckingCross) {
                                linesLeftToCross--;
                                timeToWaitBeforeCheckingCross = System.currentTimeMillis() + DELAY_FOR_MOTOR_LINE_FOLLOWER;

                                if (linesLeftToCross == 0) {
                                    timeToWait = System.currentTimeMillis() + timeAfterLastLine;
                                }
                            }

                            if (linesLeftToCross == 0 && System.currentTimeMillis() > timeToWait) {
                                isActive = false;
                            }


                            Delay.msDelay(DELAY_FOR_MOTOR_LINE_FOLLOWER);
                        }

                        motorController.stop();
                    }
                }
            } catch (Exception e) {
                Logger.error(LOG_TAG, e.toString());
                for (StackTraceElement element : e.getStackTrace()) {
                    Logger.error(LOG_TAG, element.toString());
                }
                throw e;
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
