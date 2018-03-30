/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

public interface Arm {
    void goToBoat(boolean immediateReturn);

    void goToFoodIn(boolean immediateReturn);

    void goToFoodOut(boolean immediateReturn);

    void goToFoodHanging(boolean immediateReturn);

    void goToTempReg(boolean immediateReturn);

    void goToReset(boolean immediateReturn);
}
