/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import ev3.navigation.Controller;
import lejos.robotics.chassis.Chassis;

public abstract class Robot {
    private Controller controller;

    public abstract Arm getArm();

    public abstract Chassis getChassis();

    public abstract Paddle getPaddle();

    public abstract ColorSensors getColorSensors();

    public abstract Brick getBrick();

    public Controller getController() {
        if (controller == null) {
            controller = new Controller(this);
        }

        return controller;
    }

    ;
}
