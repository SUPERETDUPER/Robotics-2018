/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.logger.Logger;
import org.jetbrains.annotations.Contract;

import java.io.OutputStream;

public class ComManager {
    private static final String LOG_TAG = ComManager.class.getSimpleName();

    private static PCDataSender dataSender;
    private static DataListener dataListener;

    public static boolean running(){
        return dataListener != null && dataSender != null;
    }

    public static boolean build() {
        OutputStream connection = PCConnection.getConnection();

        if (connection == null) {
            Logger.error(LOG_TAG, "Failed to connect to PC. Stopping program");
            return false;
        }

        dataSender = new PCDataSender(connection);
        dataListener = new DataListener(dataSender);
        dataListener.startListening();

        return true;
    }

    public static void stop() {
        dataSender.close();
        dataListener.stopListening();
    }

    @Contract(pure = true)
    public static PCDataSender getDataSender() {
        return dataSender;
    }
}
