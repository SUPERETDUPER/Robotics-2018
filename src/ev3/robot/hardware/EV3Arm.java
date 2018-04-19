/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.Robot;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Class responsible for moving the robots arm to different positions
 */
public class EV3Arm implements Robot.Arm {

    private static final EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_ARM);

    private static final int BOAT_ANGLE = 270;
    private static final int FOOD_PICK_UP_ANGLE = 0;
    private static final int FOOD_DROP_OFF_ANGLE = 200;
    private static final int FOOD_HANGING_ANGLE = 160;
    private static final int TEMP_RED_PICK_UP_ANGLE = 20;

    static {
        motor.setSpeed(10); //TODO See if possible to speed up
    }

    public void goToBoat(boolean immediateReturn) {
        motor.rotateTo(BOAT_ANGLE);
    }

    public void goToFoodIn(boolean immediateReturn) {
        motor.rotateTo(FOOD_PICK_UP_ANGLE);
    }

    public void goToFoodOut(boolean immediateReturn) {
        motor.rotateTo(FOOD_DROP_OFF_ANGLE);
    }

    public void goToFoodHanging(boolean immediateReturn) {
        motor.rotateTo(FOOD_HANGING_ANGLE);
    }

    public void goToTempReg(boolean immediateReturn) {
        motor.rotateTo(TEMP_RED_PICK_UP_ANGLE);
    }
}
