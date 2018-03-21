/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC;

import Common.Config;
import Common.Logger;
import PC.GUI.GUI;
import javafx.application.Application;

final class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(final String[] args) {
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

        new Thread() {
            @Override
            public void run() {
                super.run();
                Application.launch(GUI.class, args);
            }
        }.start();

        Connection.listen();
    }
}