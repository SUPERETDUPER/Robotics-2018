/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

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
    private static final float WHEEL_RADIUS = 10;
    private static final float AXIS_RADIUS = 20;

    private static final int SPEED = 400;

    private final MotorController motorController;

    public Chassis(MotorController motorController) {
        this.motorController = motorController;
    }

    public void travel(int distance, boolean immediateReturn) {
        int angleToRotate = (int) Math.toDegrees(distance / WHEEL_RADIUS);
        motorController.rotate(angleToRotate, angleToRotate, SPEED, SPEED, immediateReturn);
    }

    public void rotate(int angle, boolean immediateReturn) {
        int angleToTurn = (int) Math.toDegrees(Math.toRadians(angle) * AXIS_RADIUS / WHEEL_RADIUS);
        motorController.rotate(angleToTurn, -angleToTurn, SPEED, SPEED, immediateReturn);
    }

    public void arc(int angle, int radius, boolean immediateReturn) {
        int angleToTurnLeft = (int) Math.toDegrees((radius + AXIS_RADIUS) * Math.toRadians(angle) / WHEEL_RADIUS);
        int angleToTurnRight = (int) Math.toDegrees((radius - AXIS_RADIUS) * Math.toRadians(angle) / WHEEL_RADIUS);
        int speedLeft = angleToTurnLeft * 2 * SPEED / (angleToTurnLeft + angleToTurnRight);
        int speedRight = 2 * SPEED - speedLeft;

        motorController.rotate(angleToTurnLeft, angleToTurnRight, speedLeft, speedRight, immediateReturn);
    }

    public void startVelocityMode() {
        motorController.forward(SPEED);
    }

    /**
     * Correction of zero means the robot goes straight.
     * Negative correction turns left and positive turns right.
     */
    public void setVelocity(int correction) {
        motorController.setSpeed(SPEED + correction, SPEED - correction);
    }

    public void stopVelocityMode() {
        motorController.stop();
    }
}
