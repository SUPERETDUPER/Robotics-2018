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

    private static Connection connection = new Connection();

    public static void main(String[] args) {
        Config.runningOnEV3 = false;

        if (!Config.usePC) {
            Logger.error(LOG_TAG, "Config var 'usePC' isFalse");
            return;
        }

        boolean success = connection.connect();

        if (!success){
            Logger.error(LOG_TAG, "Could not connect to EV3");
            return;
        }

        GUI.init();

        try {
            DataReceiver.monitorForData(connection.getDataInputStream());
        } catch (IOException e) {
            Logger.warning(LOG_TAG, "Lost connection to EV3");
        } finally {
            cleanup();
        }
    }

    private static void cleanup(){
        connection.close();
        GUI.close();
    }
}