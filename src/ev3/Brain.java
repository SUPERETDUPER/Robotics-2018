/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.logger.Logger;
import ev3.localization.RobotPoseProvider;
import ev3.navigation.Controller;
import ev3.navigation.MapOperations;
import ev3.navigation.Offset;
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

        MapOperations pathCalculator = new MapOperations(controller.getNavigator().getPoseProvider());

        //Go to each food container
        controller.followPath(pathCalculator.getPathToContainerBottomRight(), Offset.CONTAINER_COLOR_SENSOR);
        controller.moveForward();
        pickupFood(robot.getColorSensors().getColorContainer());

        controller.followPath(pathCalculator.getPathToContainerBottomLeft(), Offset.CONTAINER_COLOR_SENSOR);
        controller.moveForward();
        pickupFood(robot.getColorSensors().getColorContainer());

        controller.followPath(pathCalculator.getPathToContainerTopLeft(), Offset.CONTAINER_COLOR_SENSOR);
        controller.moveForward();
        pickupFood(robot.getColorSensors().getColorContainer());

        if(index!=3){
            controller.followPath(pathCalculator.getPathToContainerTopRight(), Offset.CONTAINER_COLOR_SENSOR);
            controller.moveForward();
            pickupFood(robot.getColorSensors().getColorContainer());
        }

        controller.followPath(pathCalculator.getPathToBoatOne(),Offset.CONTAINER_COLOR_SENSOR);

        //Drop off food container at temp reg area
        for (int i = 0; i < 3; i++) {
            switch (listFoodColor[i]) {
                case Color.BLUE:
                    controller.followPath(pathCalculator.getPathToTempRegBlue());
                    break;
                case Color.GREEN:
                    controller.followPath(pathCalculator.getPathToTempRegGreen());
                    break;
                case Color.YELLOW:
                    controller.followPath(pathCalculator.goToTempRegYellow());
                    break;
                case Color.RED:
                    controller.followPath(pathCalculator.getPathToTempRegRed());
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
