/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import org.jetbrains.annotations.Contract;

import java.io.OutputStream;

public class ComManager {
    private static final String LOG_TAG = ComManager.class.getSimpleName();

    private static PCDataSender dataSender;
    private static DataListener dataListener;

    public static void build() {
        OutputStream connection = PCConnection.getConnection();

        dataSender = new PCDataSender(connection);
        dataListener = new DataListener(dataSender);

        dataListener.startListening();
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
