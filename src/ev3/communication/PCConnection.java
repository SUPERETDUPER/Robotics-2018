/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.Config;
import common.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

public class PCConnection {
    private static final String LOG_TAG = PCConnection.class.getSimpleName();

    @NotNull
    public static OutputStream getConnection() {
        Logger.info(LOG_TAG, "Waiting for pc to getConnection...");

        OutputStream outputStream;

        try {
            outputStream = new ServerSocket(Config.PORT_TO_CONNECT_ON_EV3).accept().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException("Could not connect to EV3");
        }

        Logger.info(LOG_TAG, "Connected to Robotics2018.pc");

        return outputStream;
    }
}
