/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import common.logger.Logger;
import ev3.robot.Robot;

/**
 * Simulates the robots arm by just printing a log message for each movement
 */
class SimArm implements Robot.Arm {
    private static final String LOG_TAG = SimArm.class.getSimpleName();

    @Override
    public void goToBoat(boolean immediateReturn) {
        Logger.info(LOG_TAG, "Moving arm to boat");
    }

    @Override
    public void goToFoodIn(boolean immediateReturn) {
        Logger.info(LOG_TAG, "Moving arm to food in");
    }

    @Override
    public void goToFoodOut(boolean immediateReturn) {
        Logger.info(LOG_TAG, "Moving arm to food out");
    }

    @Override
    public void goToFoodHanging(boolean immediateReturn) {
        Logger.info(LOG_TAG, "Moving arm to food hanging");
    }

    @Override
    public void goToTempReg(boolean immediateReturn) { Logger.info(LOG_TAG, "Moving arm to temp reg"); }

    @Override
    public void goToReset(boolean immediateReturn) {
        Logger.info(LOG_TAG, "Reset arm to base");
}

}
