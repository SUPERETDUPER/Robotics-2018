/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC;

import Common.Config;
import Common.Logger;
import PC.GUI.GUI;

import java.io.IOException;

final class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(String[] args) {
        Config.runningOnEV3 = false;

        if (!Config.usePC) {
            Logger.error(LOG_TAG, "Config var 'usePC' isFalse");
        }

        DataReceiver.connect();
        GUI.init();

        try {
            DataReceiver.monitorForData();
        } catch (IOException e) {
            Logger.warning(LOG_TAG, "Lost connection to EV3");
            DataReceiver.close();
            GUI.close();
        }
    }
}