/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import ev3.robot.EV3Robot;

/**
 * Specifies the sequence of actions the robot should do to win the competition !
 */
class Brain {
    private static final String LOG_TAG = Brain.class.getSimpleName();

    private final EV3Robot robot;
    private final Controller controller;

    Brain(EV3Robot robot, Controller controller) {
        this.robot = robot;
        this.controller = controller;
    }

    void start() {
        controller.jumpStart(); //Leave starting area
        controller.followLine(true, 1); //Go to line
        controller.turn90(true); //Turn right
        controller.followLine(true, 1); // Go to next line
        robot.getClaw().drop(true); //Lower claw
        controller.goToTempReg(true, false); //Go to temp reg
        robot.getClaw().raise();
        controller.goBackTempReg(true, false); //Go back to line
    }

    void test(){
        controller.turn90(true);
    }
}
