/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

/**
 * Class responsible for moving the robots arm to different positions
 */
public class EV3Arm {
    //TODO Test angle to be correct
    private static final int ANGLE = 10;
    private static final int SPEED = 200;

    private final EV3MediumRegulatedMotor motor = new EV3MediumRegulatedMotor(Ports.MOTOR_ARM);

    EV3Arm() {
        motor.setSpeed(SPEED);
    }

    public void drop() {
        motor.rotate(ANGLE);
    }

    public void raise() {
        motor.rotate(-ANGLE);
    }
}
