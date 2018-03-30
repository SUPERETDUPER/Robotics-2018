/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.*;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.chassis.Chassis;

public class EV3Robot implements Robot{
    private static final double WHEEL_OFFSET = 81.5;
    private static final double WHEEL_DIAMETER = 55.9;

    @Override
    public Arm getArm() {
        return new EV3Arm();
    }

    @Override
    public Chassis getChassis() {
        return Util.buildChassis(
                new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_LEFT),
                new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_RIGHT),
                WHEEL_DIAMETER,
                WHEEL_OFFSET
        );
    }

    @Override
    public Paddle getPaddle() {
        return new EV3Paddle();
    }

    @Override
    public ColorSensors getColorSensors() {
        return new EV3ColorSensors();
    }

    @Override
    public Brick getBrick() {
        return new EV3Brick();
    }
}
