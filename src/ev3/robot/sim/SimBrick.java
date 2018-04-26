/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import common.logger.Logger;
import ev3.robot.Robot;

import java.io.IOException;

/**
 * Simulates the brick methods
 */
class SimBrick implements Robot.Brick {
    private static final String LOG_TAG = SimBrick.class.getSimpleName();

    @Override
    public void waitForUserConfirmation() {
        try {
            System.out.println("Press enter to continue");
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
        } catch (IOException e) {
            Logger.error(LOG_TAG, e.toString());
        }
    }

    @Override
    public void beep() {
        Logger.info(LOG_TAG, "Beeping");
    }

    @Override
    public void buzz() {
        Logger.info(LOG_TAG, "Buzzing");
    }
}
