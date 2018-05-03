/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import java.util.ArrayList;
import java.util.List;

public class Chassis {
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

    //Everything in millimeters
    private static final float WHEEL_RADIUS = 100;
    private static final float AXIS_RADIUS = 20;

    private static final int SPEED = 400;

    private final MotorController motorController;
    private List<Move> moves = new ArrayList<>();

    public Chassis(MotorController motorController) {
        this.motorController = motorController;
        new ChassisThread().start();
    }

    public void startMoves(List<Move> moves, boolean immediateReturn){
        this.moves = moves;

        if (!immediateReturn) waitForComplete();
    }

    public void waitForComplete(){
        while (!moves.isEmpty()) Thread.yield();
    }

    private class ChassisThread extends Thread {
        @Override
        public void run() {
            while (true){
                if (!moves.isEmpty()){
                    Move move = moves.get(0);
                    moves.remove(0);

                    switch (move.getType()){
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
            int angleToTurnLeft = (int) Math.toDegrees((radius + AXIS_RADIUS) * Math.toRadians(angle) / WHEEL_RADIUS);
            int angleToTurnRight = (int) Math.toDegrees((radius - AXIS_RADIUS) * Math.toRadians(angle) / WHEEL_RADIUS);
            int speedLeft = Math.abs(angleToTurnLeft * 2 * SPEED / (angleToTurnLeft + angleToTurnRight));
            int speedRight = 2 * SPEED - speedLeft;

            motorController.setSpeed(speedLeft, speedRight);
            motorController.rotate(angleToTurnLeft, angleToTurnRight, immediateReturn);
        }
    }
}
