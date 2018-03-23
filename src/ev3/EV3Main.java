/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import util.Config;
import ev3.hardware.Brick;

final class EV3Main {
    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    public static void main(String[] args) {
        if (Config.currentMode == Config.Mode.LINKED || Config.currentMode == Config.Mode.SIM) {
            DataSender.connect(); //Try to connect to pc
        }

        Brain.start();
        Brick.waitForUserConfirmation(); //And wait for complete
    }
}