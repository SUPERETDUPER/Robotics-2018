/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import util.Config;
import ev3.hardware.Brick;

final class EV3Main {
    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    public static void main(String[] args) {
        Config.runningOnEV3 = true; //DO NOT REMOVE

        if (Config.usePC) {
            DataSender.connect(); //Try to connect to pc
        }

        Brain.start();
        Brick.waitForUserConfirmation(); //And wait for complete
    }
}