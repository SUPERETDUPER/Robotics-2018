/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.logger.Logger;
import ev3.navigation.Controller;
import ev3.navigation.MapOperations;
import ev3.robot.Robot;
import lejos.robotics.Color;

/**
 * Specifies the sequence of actions the robot should do to win the competition !
 */
class Brain {
    private static final String LOG_TAG = Brain.class.getSimpleName();

    private final int[] listFoodColor = {Color.NONE, Color.NONE, Color.NONE};
    private int index = 0;

    private final Robot robot;
    private final Controller controller;

    Brain(Robot robot, Controller controller) {
        this.robot = robot;
        this.controller = controller;
    }

    void start() {
        robot.getPaddle().move(true); //To drop conveyor belt

        //Go to each food container
        controller.followPath(MapOperations.getPathToContainerBottomRight(controller.getPose()));
        pickupFood(robot.getColorSensors().getColorContainer());

        controller.followPath(MapOperations.getPathToContainerBottomLeft(controller.getPose()));
        pickupFood(robot.getColorSensors().getColorContainer());

        controller.followPath(MapOperations.getPathToContainerTopLeft(controller.getPose()));
        pickupFood(robot.getColorSensors().getColorContainer());

        if(index!=3){
            controller.followPath(MapOperations.getPathToContainerTopRight(controller.getPose()));
            pickupFood(robot.getColorSensors().getColorContainer());
        }

        //Drop off food container at temp reg area
        for (int i = 0; i < 3; i++) {
            switch (listFoodColor[i]) {
                case Color.BLUE:
                    controller.followPath(MapOperations.getPathToTempRegBlue(controller.getPose()));
                    break;
                case Color.GREEN:
                    controller.followPath(MapOperations.getPathToTempRegGreen(controller.getPose()));
                    break;
                case Color.YELLOW:
                    controller.followPath(MapOperations.goToTempRegYellow(controller.getPose()));
                    break;
                case Color.RED:
                    controller.followPath(MapOperations.getPathToTempRegRed(controller.getPose()));
                    break;
                default:
                    Logger.warning(LOG_TAG, "Could not find temp reg of color : " + listFoodColor[i]);
            }
        }

        //TODO finish brain

        //go to boats and drop of stuff
    }

    private void pickupFood(int color) {
        if (color != Color.NONE) {
            listFoodColor[index++] = color;
            robot.getPaddle().hitBlock(true);

            if (index == 1) {
                robot.getArm().goToFoodHanging(true);
            }
        }
    }
}
