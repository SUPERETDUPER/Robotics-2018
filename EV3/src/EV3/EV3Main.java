/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3;

import Common.Config;
import EV3.hardware.Brick;

final class EV3Main {
    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    public static void main(String[] args) {
        Config.runningOnEV3 = true; //DO NOT REMOVE

        if (Config.usePC) {
            DataSender.connect(); //Try to connect to PC
        }

        Brain.start();
        Brick.waitForUserConfirmation(); //And wait for complete
    }
}