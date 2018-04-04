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
     * @param ipAddress ip address of where to connect
     */
    @NotNull
    public static InputStream getInputStream(String ipAddress) {
        Logger.info(LOG_TAG, "Attempting to getOutputStream to EV3 ...");
        for (int attempt = 0; attempt < 6; attempt++) {
            try {
                InputStream inputStream = new Socket(ipAddress, Config.PORT_TO_CONNECT_ON_EV3).getInputStream();

                Logger.info(LOG_TAG, "Connected to PCDataSender");

                return inputStream;

            } catch (IOException e) {
                Delay.msDelay(3000);
            }
        }

        Logger.error(LOG_TAG, "Failed to getOutputStream");

        throw new RuntimeException("Failed to getOutputStream");
    }

    @Contract(pure = true)
    @NotNull
    public static String getIPAddress() {
        return Config.currentMode == Config.Mode.SIM ? "localhost" : Config.EV3_IP_ADDRESS;
    }
}
