/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.Logger;
import ev3.localization.RobotPoseProvider;
import ev3.navigation.Controller;
import ev3.navigation.MapOperations;

class Brain {
    private static final String LOG_TAG = Brain.class.getSimpleName();

    static void start() {
        Controller.init();
        RobotPoseProvider.get().sendCurrentPoseToPC();

        MapOperations.goToContainerBottomLeft();
        MapOperations.goToContainerBottomRight();
        MapOperations.goToContainerTopLeft();
        MapOperations.goToContainerTopRight();

        MapOperations.goToTempRegBlue();
        MapOperations.goToTempRegGreen();
        MapOperations.goToTempRegYellow();
        MapOperations.goToTempRegRed();

        Controller.get().waitForStop();

        Logger.info(LOG_TAG, RobotPoseProvider.get().getPose().toString());
    }
}
