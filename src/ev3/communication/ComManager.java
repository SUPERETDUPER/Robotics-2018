/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.logger.LogMessage;
import common.logger.LogMessageListener;
import common.logger.Logger;
import lejos.robotics.Transmittable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;

/**
 * Singleton pattern
 * Used by the EV3 to send data to the PC
 * Manages all the data being sent away from the EV3
 * <p>
 * Used Singleton because should be accessible no matter what
 */
public class ComManager {
    // --Commented out by Inspection (25/04/18 8:37 PM):private static final String LOG_TAG = ComManager.class.getSimpleName();

    //Used to send the data
    @Nullable
    private static PCDataSender dataSender;

    /**
     * Setups the object. If not called ComManager does nothing.
     */
    public static void enable(OutputStream outputStream) {
        dataSender = new PCDataSender(outputStream);

        dataSender.setOnLostConnection(new PCDataSender.LostConnectionListener() {
            @Override
            public void lostConnection() {
                stop();
            }
        });

        Logger.setListener(new LogMessageListener() {
            @Override
            public void notifyLogMessage(LogMessage logMessage) {
                dataSender.sendLogMessage(logMessage);
            }
        });
    }

    /**
     * Stops/Disables the ComManager
     */
    public static void stop() {
        if (dataSender != null) {
            Logger.removeListener();
            dataSender.close();
            dataSender = null;
        }
    }
}
