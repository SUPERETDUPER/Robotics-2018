/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.communication;

import common.Config;
import common.logger.Logger;
import lejos.utility.Delay;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Provides the Input Stream of the connection with the EV3
 */
public class Connection {
    private static final String LOG_TAG = Connection.class.getSimpleName();

    /**
     * @return the input stream of the connection
     */
    @NotNull
    public static InputStream getInputStream(Config.Mode currentMode) {
        Logger.info(LOG_TAG, "Attempting to createOutputStream to EV3 ...");
        for (int attempt = 0; attempt < 15; attempt++) {
            try {
                InputStream inputStream = new Socket(getIPAddress(currentMode), Config.PORT_TO_CONNECT_ON_EV3).getInputStream();

                Logger.info(LOG_TAG, "Connected to PCDataSender");

                return inputStream;

            } catch (IOException e) {
                Delay.msDelay(2000);
            }
        }

        Logger.error(LOG_TAG, "Failed to createOutputStream");

        throw new RuntimeException("Failed to createOutputStream");
    }

    @Contract(pure = true)
    @NotNull
    private static String getIPAddress(Config.Mode currentMode) {
        return currentMode == Config.Mode.SIM ? "localhost" : Config.EV3_IP_ADDRESS;
    }
}
