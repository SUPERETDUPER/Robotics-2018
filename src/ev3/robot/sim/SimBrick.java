/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import common.logger.Logger;
import ev3.robot.Brick;

import java.io.IOException;

class SimBrick implements Brick {
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
}
