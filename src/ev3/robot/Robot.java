/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;

/**
 * Interface defining everything a robot should do
 */
public interface Robot {
    Arm getArm();

    RegulatedMotor getLeftMotor();
    RegulatedMotor getRightMotor();

    ColorSensors getColorSensors();

    Brick getBrick();

    /**
     * Sets up all the sensors in a background thread since this can take time
     */
    void setup();

    /**
     * @return whether all the sensors are loaded
     */
    boolean isSetup();

    interface Arm {
        void drop();
        void raise();
    }

    interface Brick {
        void waitForUserConfirmation();

        void beep();

        void buzz();
    }

    interface ColorSensors {
        float getColorSurfaceLeft();

        float getColorSurfaceRight();

        int getColorContainer();

        int getColorBoat();

        void setup();

        boolean isSetup();
    }
}
