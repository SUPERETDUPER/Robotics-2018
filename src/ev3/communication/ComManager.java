/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.TransmittableType;
import common.logger.Logger;
import lejos.robotics.Transmittable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

/**
 * Singleton pattern
 * Used by the EV3 to send data to the PC
 * Manages all the data being sent away from the EV3
 * <p>
 * Used Singleton because should be accessible no matter what
 */
public class ComManager {
    private static final String LOG_TAG = ComManager.class.getSimpleName();

    //Used to send the data
    @Nullable
    private static DataSender dataSender;

    //Monitors for data
    @Nullable
    private static DataListener dataListener;

    /**
     * Setups the object. If not called ComManager does nothing.
     */
    public static void enable(OutputStream outputStream) {
        dataSender = new PCDataSender(outputStream);

        dataSender.setOnLostConnection(new DataSender.LostConnectionListener() {
            @Override
            public void lostConnection() {
                stop();
            }
        });

        dataListener = new DataListener(dataSender);
        dataListener.startListening();
    }

    /**
     * Stops/Disables the ComManager
     */
    public static void stop() {
        if (dataSender != null) {
            dataSender.close();
            dataSender = null;
        }

        if (dataListener != null) {
            dataListener.stopListening();
            dataListener = null;
        }
    }

    /**
     * Called to send data
     */
    public static void sendTransmittable(TransmittableType type, Transmittable transmittable) {
        if (dataSender != null) {
            dataSender.sendTransmittable(type, transmittable);
        }
    }

    @Contract(pure = true)
    @Nullable
    public static DataListener getDataListener() {
        return dataListener;
    }

    /**
     * Creates a server socket and gets its output stream
     *
     * @param port the port to use
     */
    @NotNull
    public static OutputStream createOutputStream(int port) {
        Logger.info(LOG_TAG, "Waiting for PC to connect to EV3...");

        OutputStream outputStream;

        try {
            outputStream = new ServerSocket(port).accept().getOutputStream();
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Could not connect to PC");
            throw new RuntimeException("Could not connect to PC");
        }

        Logger.info(LOG_TAG, "PC connected to EV3");

        return outputStream;
    }
}
