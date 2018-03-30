/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.Paddle;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;

public class EV3Paddle implements Paddle{
    private static final EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_PADDLE);

    static{

        motor.setSpeed(motor.getMaxSpeed());
    }


    public void useMotor(boolean immediateReturn) {
        motor.rotate(2880);
    }
}
