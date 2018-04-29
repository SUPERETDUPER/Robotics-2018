/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.logger.Logger;
import ev3.robot.Robot;
import lejos.robotics.Color;

/**
 * Specifies the sequence of actions the robot should do to win the competition !
 */
class Brain {
    private static final String LOG_TAG = Brain.class.getSimpleName();

    private final Robot robot;

    Brain(Robot robot) {
        this.robot = robot;
    }

    void start() {

    }
}
