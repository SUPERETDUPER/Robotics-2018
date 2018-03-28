/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.Config;
import common.LogMessageListener;
import common.Logger;
import org.jetbrains.annotations.NotNull;

public class DataListener {
    private final DataSender sender;

    public DataListener(final DataSender sender) {
        this.sender = sender;

        //Attach the listener if not using the simulator so that log messages are sent
        if (Config.currentMode == Config.Mode.DUAL) {
            Logger.setListener(new LogMessageListener() {
                @Override
                public void notifyLogMessage(@NotNull String message) {
                    sender.sendLogMessage("From EV3 : " + message);
                }
            });
        }
    }
}
