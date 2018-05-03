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

    private static final int DISTANCE_TO_CLEAR_STARTING_AREA = 250;

    private final Chassis chassis;
    private final LineFollower lineFollower;

    Controller(EV3Robot robot) {
        MotorController motorController = new MotorController(robot.getLeftMotor(), robot.getRightMotor());
        this.chassis = new Chassis(motorController);
        this.lineFollower = new LineFollower(motorController, robot);
    }

    public LineFollower getLineFollower() {
        return lineFollower;
    }

    public Chassis getChassis() {
        return chassis;
    }

    void goToStartIntersection() {
        chassis.startMoves(Arrays.asList(
                Move.travel(DISTANCE_TO_CLEAR_STARTING_AREA)
        ), false);

        lineFollower.startLineFollower(true, false, 1, 0, false);

        chassis.startMoves(Arrays.asList(
                Move.rotate(90)
        ), false);

        lineFollower.startLineFollower(true, true, 1, 0, false);
    }

    void goToTempRegGreen(){
        chassis.startMoves(Arrays.asList(
                Move.travel(-10),
                Move.arc(-90, -10),
                Move.travel(10)
        ), false);
    }

    void goToBoatsWithGreen(){
        chassis.startMoves(Arrays.asList(
                Move.arc(-90, 10)
        ), false);

        lineFollower.startLineFollower(true, false, 3,0, false);
    }
}
