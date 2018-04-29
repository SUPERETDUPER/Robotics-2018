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

    private static final int ANGLE = 10;
    private static final int SPEED = 200;

    static {
        motor.setSpeed(SPEED);
    }

    @Override
    public void drop() {
        motor.rotate(ANGLE);
    }

    @Override
    public void raise() {
        motor.rotate(-ANGLE);
    }
}
