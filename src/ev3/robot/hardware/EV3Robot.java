/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.navigation.Controller;
import ev3.robot.*;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.chassis.Chassis;

public class EV3Robot implements Robot {

    private EV3Arm arm;
    private EV3Paddle paddle;
    private EV3ColorSensors colorSensors;
    private EV3Brick brick;
    private Chassis chassis;

    @Override
    public Arm getArm() {
        if (arm == null) {
            arm = new EV3Arm();
        }

        return arm;
    }

    @Override
    public Chassis getChassis() {
        if (chassis == null) {
            chassis = Util.buildChassis(
                    new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_LEFT),
                    new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_RIGHT)
            );
        }

        return chassis;
    }

    @Override
    public Paddle getPaddle() {
        if (paddle == null) {
            paddle = new EV3Paddle();
        }

        return paddle;
    }

    @Override
    public ColorSensors getColorSensors() {
        if (colorSensors == null) {
            colorSensors = new EV3ColorSensors();
        }

        return colorSensors;
    }

    @Override
    public Brick getBrick() {
        if (brick == null) {
            brick = new EV3Brick();
        }

        return brick;
    }

    @Override
    public Controller getController() {
        return new Controller(this);
    }
}
