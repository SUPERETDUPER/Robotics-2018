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

    private int[] listFoodColor = new int[3];
    private int index = 0;
    private Robot robot;

    public Brain(Robot robot) {
        this.robot = robot;
    }

    void start() {
        Controller controller = robot.getController();

        robot.getArm().goToFoodIn(true);

        controller.followPath(MapOperations.goToContainerBottomRight(controller.getPose()));
        pickupFood(robot.getColorSensors().getColorContainer());

        controller.followPath(MapOperations.goToContainerBottomLeft(controller.getPose()));
        pickupFood(robot.getColorSensors().getColorContainer());

        controller.followPath(MapOperations.getPathToContainerTopLeft(controller.getPose()));
        pickupFood(robot.getColorSensors().getColorContainer());

        controller.followPath(MapOperations.getPathToContainerTopRight(controller.getPose()));
        pickupFood(robot.getColorSensors().getColorContainer());

        for (int i = 0; i < 3; i++) {
            switch (listFoodColor[i]) {
                case Color.BLUE:
                    controller.followPath(MapOperations.goToTempRegBlue(controller.getPose()));
                    break;
                case Color.GREEN:
                    controller.followPath(MapOperations.goToTempRegGreen(controller.getPose()));
                    break;
                case Color.YELLOW:
                    controller.followPath(MapOperations.goToTempRegYellow(controller.getPose()));
                    break;
                case Color.RED:
                    controller.followPath(MapOperations.goToTempRegRed(controller.getPose()));
            }
        }

        Logger.info(LOG_TAG, controller.getPose().toString());
    }

    private void pickupFood(int color) {
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
