/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.Config;
import common.Logger;

import java.io.InputStream;

final class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(final String[] args) {
        if (Config.currentMode == Config.Mode.SOLO) {
            Logger.error(LOG_TAG, "No PC required in mode solo");
            return;
        }

        InputStream inputStream = EV3Connection.getConnection();

        if (inputStream == null) {
            Logger.error(LOG_TAG, "Could not getConnection to ev3");
            return;
        }

        DataReader.init(inputStream, GUI.listener);

        GUI.launchGUI();

        DataReader.read();
    }
}