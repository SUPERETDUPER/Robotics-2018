/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.Paddle;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class EV3Paddle implements Paddle{
    private final EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_PADDLE);

    public void moveBlockOffConveyor(boolean immediateReturn) {
        motor.rotate(720);

    }

    public void hitBlock(boolean immediateReturn) {
        motor.rotate(360);
    }
}
