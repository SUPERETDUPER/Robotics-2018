/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import lejos.hardware.motor.EV3MediumRegulatedMotor;

/**
 * Class responsible for moving the robots arm to different positions
 */
public class EV3Arm implements MotorSensor {
    //TODO Test angle to be correct
    private static final int ANGLE = 90;
    private static final int SPEED = 200;

    private EV3MediumRegulatedMotor motor;

    @Override
    public void create() {
        motor = new EV3MediumRegulatedMotor(Ports.MOTOR_ARM);
        motor.setSpeed(SPEED);
    }

    @Override
    public boolean isCreated() {
        return motor != null;
    }

    public void drop(){
        drop(false);
    }

    public void drop(boolean immediateReturn) {
        motor.rotate(ANGLE, immediateReturn);
    }

    public void raise(){
        raise(false);
    }

    public void raise(boolean immediateReturn) {
        motor.rotate(-ANGLE, immediateReturn);
    }

    public void waitComplete(){
        motor.waitComplete();
    }
}
