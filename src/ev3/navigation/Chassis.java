/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.logger.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Math for chassis class :
 * <p>
 * arc length = radius * angle (in radians)
 * Therefore
 * [distance wheel traveled] = [wheel radius] * [angle rotated]
 * <p>
 * TRAVEL
 * [distance traveled by a wheel] = [wheel radius] * [angle rotated in radians]
 * <p>
 * ROTATE
 * [angle robot rotated (in rad)] * [chassis radius] = [distance traveled by a wheel]
 * Therefore :
 * [angle rotated in radians] = [angle robot rotated(in rad)] * [chassis radius] / [wheel radius]
 * <p>
 * ARC
 * [distance traveled by right wheel] = ([arc radius] - [chassis radius]) * [angle turned in radians]
 * [distance traveled by left wheel] = ([arc radius] + [chassis radius]) * [angle turned in radians]
 * <p>
 * ARC-SPEED
 * [speed] = [distance]/[time]
 * [speed] * [time] = [wheel radius]*[angle rotated]
 * [wheel radius] * [angle rotated left] / [speed left] = [wheel radius] * [angle rotated right] / [speed right]
 * [speed right] / [speed left] = [angle rotated right] / [angle rotated left]
 * (2 * [average speed] - [speed left]) / [speed left] = [angle rotated right] / [angle rotated left]
 * 2 * [average speed] / [speed left] = [angle rotated right] / [angle rotated left] + 1
 * [speed left] = 2 * [average speed] * [angle rotated left] / ([angle rotated left] + [angle rotated right])
 */
public class Chassis {
    private static final String LOG_TAG = Chassis.class.getSimpleName();

    //Everything in millimeters
    //TODO Fine tune values
    private static final float WHEEL_RADIUS = 28;
    private static final float AXIS_RADIUS = 84;

    private static final int SPEED = 500;

    private final MotorController motorController;
    private LinkedList<Move> moves = new LinkedList<>();

    public Chassis(MotorController motorController) {
        this.motorController = motorController;
        new ChassisThread().start();
    }

    public void startMoves(List<Move> moves, boolean immediateReturn) {
        this.moves = new LinkedList<>(moves);

        if (!immediateReturn) waitForComplete();
    }

    public void waitForComplete() {
        while (!moves.isEmpty()) Thread.yield();
    }

    private class ChassisThread extends Thread {
        @Override
        public void run() {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    if (!moves.isEmpty()) {
                        Move move = moves.get(0);
                        moves.remove(0);

                        switch (move.getType()) {
                            case ARC:
                                arc(move.getAngle(), move.getRadius(), false);
                                break;
                            case ROTATE:
                                rotate(move.getAngle(), false);
                                break;
                            case TRAVEL:
                                travel(move.getDistance(), false);
                                break;
                        }
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

        private void travel(int distance, boolean immediateReturn) {
            int angleToRotate = (int) Math.toDegrees(distance / WHEEL_RADIUS);
            motorController.setSpeed(SPEED, SPEED);
            motorController.rotate(angleToRotate, angleToRotate, immediateReturn);
        }

        private void rotate(int angle, boolean immediateReturn) {
            int angleToTurn = (int) Math.toDegrees(Math.toRadians(angle) * AXIS_RADIUS / WHEEL_RADIUS);
            motorController.rotate(angleToTurn, -angleToTurn, immediateReturn);
        }

        private void arc(int angle, int radius, boolean immediateReturn) {
            if (radius < 0) angle *= -1;
            int angleToTurnLeft = (int) Math.toDegrees((radius + AXIS_RADIUS) * Math.toRadians(angle) / WHEEL_RADIUS);
            int angleToTurnRight = (int) Math.toDegrees((radius - AXIS_RADIUS) * Math.toRadians(angle) / WHEEL_RADIUS);
            int speedLeft = Math.abs(angleToTurnLeft) * 2 * SPEED / (Math.abs(angleToTurnLeft) + Math.abs(angleToTurnRight));
            int speedRight = 2 * SPEED - speedLeft;

            motorController.setSpeed(speedLeft, speedRight);
            motorController.rotate(angleToTurnLeft, angleToTurnRight, immediateReturn);
        }
    }
}
