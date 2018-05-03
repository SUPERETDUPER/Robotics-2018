/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.logger.Logger;
import ev3.navigation.Chassis;
import ev3.navigation.MotorController;
import ev3.robot.EV3Robot;
import lejos.utility.Delay;

class Controller {
    private static final int TIME_TO_CROSS_LINE = 1000;
    private static final String LOG_TAG = Controller.class.getSimpleName();


    private static final int ANGLE_TO_TURN_90 = 265;
    private static final int DISTANCE_TO_CLEAR_STARTING_AREA = 250;

    private static final int BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER = 270;
    private static final int DISTANCE_TEMP_REG_FROM_LINE = 120;
    private static final int GO_TO_TEMP_ARC_CONSTANT = 600;

    private final Chassis chassis;
    private final EV3Robot robot;

    Controller(EV3Robot robot) {
        this.robot = robot;
        this.chassis = new Chassis(new MotorController(robot.getLeftMotor(), robot.getRightMotor()));
    }

//    void arcCorner() {
//        arc(false,true, ARC_CORNER, false);
//    }

//    void arc(boolean backward, boolean toTheRight, int constant){
//        arc(backward,toTheRight,constant, false);
//    }
//
//    void arc(boolean backward, boolean toTheRight, int constant, boolean immediateReturn) {
//        int distanceLeft = ANGLE_TO_TURN_90 + constant;
//        int distanceRight = -ANGLE_TO_TURN_90 + constant;
//        int speedLeft = distanceLeft * 2 * SPEED / (distanceLeft + distanceRight);
//        int speedRight = 2 * SPEED - speedLeft;
//        int backwardSign = backward ? -1 : 1;
//
//        if (toTheRight) {
//            chassis.rotate(distanceLeft * backwardSign, distanceRight * backwardSign, speedLeft, speedRight, immediateReturn);
//        } else {
//            chassis.rotate(backwardSign * distanceRight, backwardSign * distanceLeft, speedRight, speedLeft, immediateReturn);
//        }
//    }

    void turn90(boolean turnRight) {
        turn90(turnRight, false);
    }

    void turn90(boolean turnRight, boolean immediateReturn) {
        if (turnRight) {
            chassis.rotate(ANGLE_TO_TURN_90, immediateReturn);
        } else {
            chassis.rotate(-ANGLE_TO_TURN_90, immediateReturn);
        }
    }

    void jumpStart() {
        chassis.travel(DISTANCE_TO_CLEAR_STARTING_AREA, false);
    }

    void goToTempReg(boolean isOnRightSide, boolean isInFront) {
        chassis.arc(90, 100);

        chassis.travel(DISTANCE_TEMP_REG_FROM_LINE);
    }

    void goBackTempReg(boolean isOnRightSide, boolean isInFront) {
        arc(isInFront, isOnRightSide, GO_TO_TEMP_ARC_CONSTANT);

        chassis.travel(isInFront ? -BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER : BACKUP_DISTANCE_TO_TEMP_REG_FROM_CORNER);
    }


    //MOTOR HELPER METHODS


}
