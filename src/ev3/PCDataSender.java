/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.logger.LogMessage;
import common.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class PCDataSender {
    private static final String LOG_TAG = PCDataSender.class.getSimpleName();

    @NotNull
    private final DataOutputStream dos;

    public PCDataSender(@NotNull OutputStream outputStream) {
        dos = new DataOutputStream(outputStream);
    }

    public void sendLogMessage(LogMessage message) {
        try {
            message.dumpObject(dos);
            dos.flush();
        } catch (IOException e) {
            message.printToSysOut("");

            try {
                dos.close();
            } catch (IOException ioe) {
                Logger.error(LOG_TAG, "Could not close data output stream : " + ioe);
            }

            Logger.error(LOG_TAG, "Lost connection: " + e);
        }
    }
}