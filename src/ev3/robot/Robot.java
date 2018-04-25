/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import lejos.robotics.chassis.Chassis;

/**
 * Interface defining everything a robot should do
 */
public interface Robot {
    Arm getArm();

    Chassis getChassis();

    Paddle getPaddle();

    ColorSensors getColorSensors();

    Brick getBrick();

    DistanceSensor getDistanceSensor();

    /**
     * Sets up all the sensors in a background thread since this can take time
     */
    void setup();

    /**
     * @return whether all the sensors are loaded
     */
    boolean isSetup();

    interface Arm {
        void goToBoat(boolean immediateReturn);

        void goToFoodIn(boolean immediateReturn);

        void goToFoodOut(boolean immediateReturn);

        void goToFoodHanging(boolean immediateReturn);

        void goToTempReg(boolean immediateReturn);
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

    interface Paddle {
        void move(boolean immediateReturn);

        void hitBlock(boolean immediateReturn);
    }

    interface DistanceSensor {
        float getDistance();
    }
}
