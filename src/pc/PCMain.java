/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import util.Config;
import util.Logger;
import javafx.application.Application;

final class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(final String[] args) {
        if (Config.currentMode == Config.Mode.SOLO) {
            Logger.error(LOG_TAG, "No PC required in mode solo");
            return;
        }

        boolean success = Connection.connect();

        if (!success) {
            Logger.error(LOG_TAG, "Could not connect to ev3");
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