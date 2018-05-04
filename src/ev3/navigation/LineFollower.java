/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.logger.Logger;
import ev3.robot.EV3Robot;
import lejos.utility.Delay;

public class LineFollower {
    private static final String LOG_TAG = LineFollower.class.getSimpleName();

    private static final int CORRECTION_CONSTANT_LINE_FOLLOWER = 300;
    private static final int CORRECTION_CONSTANT_MIDDLE = 300;
    private static final float BLACK_LINE_THRESHOLD = 0.6F;
    private static final int DELAY_FOR_MOTOR_LINE_FOLLOWER = 10;
    private static final int DELAY_TO_CROSS_LINE = 1000;
    private static final int SPEED = 400;
    private static final float CENTER = 0.5F;

    public enum Mode {
        LEFT,
        MIDDLE,
        RIGHT
    }

    private final MotorController motorController;
    private final EV3Robot robot;

    /**
     * Whether the line follower is following a line
     */
    private boolean isActive = false;

    private Mode mode;
    private int linesLeftToCross;
    private int timeAfterLastLine;

    public LineFollower(MotorController motorController, EV3Robot robot) {
        this.motorController = motorController;
        this.robot = robot;

        new LineFollowerThread().start();
    }

    public void startLineFollower(Mode mode, int linesToCross, int timeAfterLastLine, boolean immediateReturn) {
        this.mode = mode;
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
        long timeToWaitBeforeCheckingCross = 0;
        long timeToWait = 0;

        LineFollowerThread() {
            this.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    if (isActive) {
                        motorController.forward();

                        Delay.msDelay(DELAY_FOR_MOTOR_LINE_FOLLOWER);

                        while (isActive) {
                            switch (mode) {
                                case RIGHT:
                                case LEFT:
                                    // Error is negative when on white and positive on black
                                    int error = (int) (CORRECTION_CONSTANT_LINE_FOLLOWER * (CENTER - getLineColor()));

                                    if (shouldInverseError()) error *= -1;

                                    motorController.setSpeed(SPEED + error, SPEED - error);

                                    if (getCheckerColor() < BLACK_LINE_THRESHOLD && System.currentTimeMillis() > timeToWaitBeforeCheckingCross) {
                                        linesLeftToCross--;
                                        timeToWaitBeforeCheckingCross = System.currentTimeMillis() + DELAY_TO_CROSS_LINE;

                                        if (linesLeftToCross == 0) {
                                            timeToWait = System.currentTimeMillis() + timeAfterLastLine;
                                        }
                                    }

                                    if (linesLeftToCross == 0 && System.currentTimeMillis() >= timeToWait) {
                                        isActive = false;
                                    }
                                    break;
                                case MIDDLE:
                                    float left = robot.getColorSurfaceLeft();
                                    float right = robot.getColorSurfaceRight();
                                    int middleError = (int) (CORRECTION_CONSTANT_MIDDLE * (left - right));
                                    motorController.setSpeed(SPEED + middleError, SPEED - middleError);

                                    if (left < BLACK_LINE_THRESHOLD && right < BLACK_LINE_THRESHOLD) {
                                        isActive = false;
                                    }
                                    break;
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

        private boolean shouldInverseError() {
            return mode == Mode.LEFT;
        }

        private float getLineColor() {
            return mode == Mode.LEFT ? robot.getColorSurfaceRight() : robot.getColorSurfaceLeft();
        }

        private float getCheckerColor() {
            return mode == Mode.LEFT ? robot.getColorSurfaceLeft() : robot.getColorSurfaceRight();
        }
    }
}
