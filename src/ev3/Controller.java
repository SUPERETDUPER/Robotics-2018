/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import ev3.navigation.Chassis;
import ev3.navigation.LineFollower;
import ev3.navigation.MotorController;
import ev3.navigation.Move;
import ev3.robot.EV3Robot;

import java.util.Arrays;

class Controller {
    private static final String LOG_TAG = Controller.class.getSimpleName();

    private final Chassis chassis;
    private final LineFollower lineFollower;
    private final static int SPEED = 500;

    Controller(EV3Robot robot) {
        MotorController motorController = new MotorController(robot.getLeftMotor(), robot.getRightMotor());
        this.chassis = new Chassis(motorController);
        this.lineFollower = new LineFollower(motorController, robot);
        this.chassis.setSpeed(SPEED);
    }

    public LineFollower getLineFollower() {
        return lineFollower;
    }

    public Chassis getChassis() {
        return chassis;
    }

    void goToStartIntersection() {
        chassis.startMove(Move.travel(120), false);

        lineFollower.startLineFollower(LineFollower.Mode.RIGHT, 1, 0, false);

        chassis.startMove(Move.rotate(90), false);

        lineFollower.startLineFollower(LineFollower.Mode.MIDDLE, 1, 0, false);
    }

    void goToTempRegGreen() {
        chassis.startMoves(Arrays.asList(
                Move.travel(-130),
                Move.arc(-90, -10)
        ), false);

        chassis.setSpeed(200);
        chassis.startMove(Move.travel(40), false);
        chassis.setSpeed(SPEED);
    }

    void goToBoatsWithGreen() {
        chassis.startMove(Move.arc(-90, 10), false);

        lineFollower.startLineFollower(LineFollower.Mode.RIGHT, 3, 0, false);

        chassis.startMoves(Arrays.asList(
                Move.travel(250),
                Move.rotate(90),
                Move.travel(-50)
        ), false);

        lineFollower.startLineFollower(LineFollower.Mode.RIGHT, 0, 1000, false);
    }

    void moveAlongBotBoatsWithGreen() {
        lineFollower.startLineFollower(LineFollower.Mode.RIGHT, 0, 1000, true);
    }
}
