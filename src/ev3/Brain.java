/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.logger.Logger;
import ev3.navigation.Controller;
import ev3.navigation.MapOperations;
import ev3.robot.Robot;
import lejos.robotics.Color;

class Brain {
    private static final String LOG_TAG = Brain.class.getSimpleName();
    private static int[] listFoodColor = new int[3];
    private static int index = 0;
    private static Robot robot;

    static void start(Robot robot) {
        Brain.robot = robot;
        Controller.get().init(robot);
        Controller.get().getPose();

        robot.getArm().goToFoodIn(true);

        MapOperations.goToContainerBottomRight(Controller.get().getPose());
        pickupFood(robot.getColorSensors().getColorContainer());
        MapOperations.goToContainerBottomLeft(Controller.get().getPose());
        pickupFood(robot.getColorSensors().getColorContainer());
        MapOperations.goToContainerTopLeft(Controller.get().getPose());
        pickupFood(robot.getColorSensors().getColorContainer());
        MapOperations.goToContainerTopRight(Controller.get().getPose());
        pickupFood(robot.getColorSensors().getColorContainer());

        for (int i = 0; i < 3; i++) {
            switch (listFoodColor[i]) {
                case Color.BLUE:
                    MapOperations.goToTempRegBlue(Controller.get().getPose());
                    break;
                case Color.GREEN:
                    MapOperations.goToTempRegGreen(Controller.get().getPose());
                    break;
                case Color.YELLOW:
                    MapOperations.goToTempRegYellow(Controller.get().getPose());
                    break;
                case Color.RED:
                    MapOperations.goToTempRegRed(Controller.get().getPose());
            }


        }


        Controller.get().waitForStop();

        Logger.info(LOG_TAG, Controller.get().getPose().toString());
    }

    private static void pickupFood(int color) {
        if (color != Color.NONE) {
            listFoodColor[index] = color;
            index++;
            robot.getPaddle().hitBlock(true);
            if (index == 1) {
                robot.getArm().goToFoodHanging(true);
            }
        }
    }
}
