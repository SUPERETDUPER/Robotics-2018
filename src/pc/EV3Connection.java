/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.Config;
import common.logger.Logger;
import lejos.utility.Delay;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Provides the Input Stream of the connection with the EV3
 */
class EV3Connection {
    private static final String LOG_TAG = EV3Connection.class.getSimpleName();

    /**
     * @return the input stream of the connection
     */
    static InputStream getConnection() {
        Logger.info(LOG_TAG, "Attempting to getConnection to EV3 ...");
        for (int attempt = 0; attempt < 6; attempt++) {
            try {
                InputStream inputStream = new Socket(getIPAddress(), Config.PORT_TO_CONNECT_ON_EV3).getInputStream();

                Logger.info(LOG_TAG, "Connected to PCDataSender");

                return inputStream;

            } catch (IOException e) {
                Delay.msDelay(3000);
            }
        }

        Logger.warning(LOG_TAG, "Failed to getConnection");

        return null;
    }

    @NotNull
    private static String getIPAddress() {
        return Config.currentMode == Config.Mode.SIM ? "localhost" : Config.EV3_IP_ADDRESS;
    }
}
