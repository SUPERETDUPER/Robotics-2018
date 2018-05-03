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

    private static final int ANGLE_TO_TURN_90 = 265;
    private static final int DISTANCE_TO_CLEAR_STARTING_AREA = 250;

    private static final int BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER = 270;
    private static final int DISTANCE_TEMP_REG_FROM_LINE = 120;
    private static final int GO_TO_TEMP_ARC_CONSTANT = 600;

    private final Chassis chassis;
    private final LineFollower lineFollower;
    private final EV3Robot robot;

    Controller(EV3Robot robot) {
        this.robot = robot;
        MotorController motorController = new MotorController(robot.getLeftMotor(), robot.getRightMotor());
        this.chassis = new Chassis(motorController);
        this.lineFollower = new LineFollower(motorController, robot);
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
