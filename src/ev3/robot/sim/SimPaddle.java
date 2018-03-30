/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.sim;

import common.logger.Logger;
import ev3.robot.Paddle;

class SimPaddle implements Paddle {
    private static final String LOG_TAG = SimPaddle.class.getSimpleName();

    @Override
    public void moveBlockOffConveyor(boolean immediateReturn) {
        Logger.info(LOG_TAG, "Moving block of conveyor");
    }

    @Override
    public void hitBlock(boolean immediateReturn) {
        Logger.info(LOG_TAG, "Hitting block");
    }
}
