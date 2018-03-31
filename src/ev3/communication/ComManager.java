/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.Config;
import common.TransmittableType;
import common.logger.Logger;
import common.particles.MCLData;
import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Pose;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

public class ComManager {
    private static final String LOG_TAG = ComManager.class.getSimpleName();

    private static DataSender dataSender;
    private static DataListener dataListener;

    public static void startCommunication() {
        dataSender = new PCDataSender(getConnection());
        dataListener = new DataListener(dataSender);

        dataListener.startListening();
    }

    public static void stop() {
        dataSender.close();
        dataListener.stopListening();
    }

    public static void sendTransmittable(Transmittable transmittable) {
        TransmittableType type;

        if (transmittable instanceof MCLData) {
            type = TransmittableType.MCL_DATA;
        } else if (transmittable instanceof Path) {
            type = TransmittableType.PATH;
        } else if (transmittable instanceof Pose) {
            type = TransmittableType.CURRENT_POSE;
        } else {
            Logger.error(LOG_TAG, "Not a registered transmittable");
            return;
        }

        dataSender.sendTransmittable(type, transmittable);
    }

    @Contract(pure = true)
    public static DataListener getDataListener() {
        return dataListener;
    }

    @NotNull
    public static OutputStream getConnection() {
        Logger.info(LOG_TAG, "Waiting for PC to connect to EV3...");

        OutputStream outputStream;

        try {
            outputStream = new ServerSocket(Config.PORT_TO_CONNECT_ON_EV3).accept().getOutputStream();
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Could not connect to PC");
            throw new RuntimeException("Could not connect to PC");
        }

        Logger.info(LOG_TAG, "PC connected to EV3");

        return outputStream;
    }
}
