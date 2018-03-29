/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.Arm;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class EV3Arm implements Arm {

    private static EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_ARM);
    private static int startingAngle = 0;
    private static int boatAngle = 270;
    private static int foodPickUpAngle = 20;
    private static int foodDropOffAngle = 200;
    private static int foodHangingAngle = 160;
    private static int tempRedPickUpAngle = 20;

    static {
        motor.setSpeed(10);
    }

    public void goToBoat(boolean immediateReturn) {
        motor.rotate(boatAngle);
    }

    public void goToFoodIn(boolean immediateReturn) {
        motor.rotate(foodPickUpAngle);
    }

    public void goToFoodOut(boolean immediateReturn) {
        motor.rotate(foodDropOffAngle);
    }

    public void goToFoodHanging(boolean immediateReturn) {
        motor.rotate(foodHangingAngle);
    }

    public void goToTempReg(boolean immediateReturn) {
        motor.rotate(tempRedPickUpAngle);
    }
    public void reset(boolean immediateReturn){
        motor.rotateTo(startingAngle);

    }

}
