/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.logger.LogMessage;
import common.logger.Logger;
import lejos.robotics.Transmittable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

final class PCDataSender {
    interface LostConnectionListener {
        void lostConnection();
    }


    private static final String LOG_TAG = PCDataSender.class.getSimpleName();

    @NotNull
    private final DataOutputStream dos;

    @Nullable
    private LostConnectionListener lostConnectionListener;

    PCDataSender(@NotNull OutputStream outputStream) {
        dos = new DataOutputStream(outputStream);
    }

    void sendLogMessage(LogMessage message) {
        try {
            message.dumpObject(dos);
            dos.flush();
        } catch (IOException e) {
            if (lostConnectionListener != null) {
                lostConnectionListener.lostConnection();
            }

            close();

            Logger.error(LOG_TAG, "Lost connection");
        }
    }

    void setOnLostConnection(@Nullable LostConnectionListener lostConnectionListener) {
        this.lostConnectionListener = lostConnectionListener;
    }

    void close() {
        try {
            dos.close();
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Could not close data output stream");
        }
    }
}