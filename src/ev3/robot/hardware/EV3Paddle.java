/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.Paddle;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class EV3Paddle implements Paddle{
    private final EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_PADDLE);

    public void move(boolean immediateReturn) {
        motor.setSpeed(250);
        motor.rotate(120);
        motor.setSpeed(motor.getMaxSpeed());
        motor.rotate(2540);

    }

    public void hitBlock(boolean immediateReturn) {
        motor.rotate(360);
    }
}
