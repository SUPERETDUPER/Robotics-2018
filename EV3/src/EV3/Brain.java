/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3;

import Common.Logger;
import EV3.localization.RobotPoseProvider;
import EV3.navigation.Controller;
import EV3.navigation.MapOperations;

class Brain {
    private static final String LOG_TAG = Brain.class.getSimpleName();

    static void start() {
        Controller.init();

        MapOperations.goToTempRegBlue();
        Controller.get().getNavigator().followPath();
        Controller.get().waitForStop();

        MapOperations.goToTempRegGreen();
        Controller.get().getNavigator().followPath();
        Controller.get().waitForStop();

        MapOperations.goToTempRegYellow();
        Controller.get().getNavigator().followPath();
        Controller.get().waitForStop();

        Logger.info(LOG_TAG, RobotPoseProvider.get().getPose().toString());
    }
}
