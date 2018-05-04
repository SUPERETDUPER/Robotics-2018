/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import lejos.hardware.motor.EV3MediumRegulatedMotor;

/**
 * Class responsible for moving the robots arm to different positions
 */
public class EV3Claw implements MotorSensor {
    private static final int ANGLE = 750;

    private EV3MediumRegulatedMotor motor;

    @Override
    public void create() {
        motor = new EV3MediumRegulatedMotor(Ports.MOTOR_CLAW);
        motor.setSpeed((int) motor.getMaxSpeed());
    }

    @Override
    public boolean isNotCreated() {
        return motor == null;
    }

    public void drop(boolean immediateReturn) {
        motor.rotate(-ANGLE, immediateReturn);
    }

    public void raise(boolean immediateReturn) {
        motor.rotate(ANGLE, immediateReturn);
    }

    public void waitComplete(){
        motor.waitComplete();
    }
}
