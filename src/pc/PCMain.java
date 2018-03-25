/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.Config;
import common.Logger;

final class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(final String[] args) {
        if (Config.currentMode == Config.Mode.SOLO) {
            Logger.error(LOG_TAG, "No PC required in mode solo");
            return;
        }

        boolean successfullyConnected = Connection.connect();

        if (!successfullyConnected) {
            Logger.error(LOG_TAG, "Could not connect to ev3");
            return;
        }

        GUI.launchGUI();

        Connection.listen(GUI.listener);
    }
}