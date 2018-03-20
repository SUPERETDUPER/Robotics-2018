/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC;

import Common.Config;
import Common.Logger;

final class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(String[] args) {
        Config.runningOnEV3 = false;

        if (!Config.usePC) {
            Logger.error(LOG_TAG, "Config var 'usePC' isFalse");
            return;
        }

        boolean success = Connection.connect();

        if (!success) {
            Logger.error(LOG_TAG, "Could not connect to EV3");
            return;
        }

        GUI.init();
        Connection.listen();
    }
}