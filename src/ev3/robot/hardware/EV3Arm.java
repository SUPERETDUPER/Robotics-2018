/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.Arm;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class EV3Arm implements Arm {

    private static final EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_ARM);

    private static final int boatAngle = 270;
    private static final int foodPickUpAngle = 20;
    private static final int foodDropOffAngle = 200;
    private static final int foodHangingAngle = 160;
    private static final int tempRedPickUpAngle = 20;

    static {
        motor.setSpeed(10);
    }

    public void goToBoat(boolean immediateReturn) {
        motor.rotateTo(boatAngle);
    }

    public void goToFoodIn(boolean immediateReturn) {
        motor.rotateTo(foodPickUpAngle);
    }

    public void goToFoodOut(boolean immediateReturn) {
        motor.rotateTo(foodDropOffAngle);
    }

    public void goToFoodHanging(boolean immediateReturn) {
        motor.rotateTo(foodHangingAngle);
    }

    public void goToTempReg(boolean immediateReturn) {
        motor.rotateTo(tempRedPickUpAngle);
    }
}
