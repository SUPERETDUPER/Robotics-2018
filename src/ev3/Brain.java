/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.logger.Logger;
import ev3.navigation.Controller;
import ev3.navigation.MapOperations;
import ev3.robot.Robot;

class Brain {
    private static final String LOG_TAG = Brain.class.getSimpleName();

    static void start(Robot robot) {
        Controller.get().init(robot);
        Controller.get().getPose();

        MapOperations.goToContainerBottomLeft(Controller.get().getPose());
        MapOperations.goToContainerBottomRight(Controller.get().getPose());
        MapOperations.goToContainerTopLeft(Controller.get().getPose());
        MapOperations.goToContainerTopRight(Controller.get().getPose());

        MapOperations.goToTempRegBlue(Controller.get().getPose());
        MapOperations.goToTempRegGreen(Controller.get().getPose());
        MapOperations.goToTempRegYellow(Controller.get().getPose());
        MapOperations.goToTempRegRed(Controller.get().getPose());

        Controller.get().waitForStop();

        Logger.info(LOG_TAG, Controller.get().getPose().toString());
    }
}
