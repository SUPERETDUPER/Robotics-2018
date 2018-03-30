/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.Config;
import common.TransmittableType;
import common.logger.LogMessageListener;
import common.logger.Logger;
import common.particles.MCLData;
import ev3.localization.MCLDataListener;
import ev3.localization.RobotPoseProvider;

public class DataListener implements MCLDataListener, LogMessageListener{
    private final DataSender sender;

    DataListener(final DataSender sender) {
        this.sender = sender;
    }

    void startListening() {
        //Attach the listener if not using the simulator so that log messages are sent
        if (Config.currentMode == Config.Mode.DUAL) {
            Logger.setListener(this);
        }

        RobotPoseProvider.get().addListener(this);
    }

    void stopListening() {
        Logger.removeListener();

        RobotPoseProvider.get().removeListener(this);
    }

    @Override
    public void notifyLogMessage(String message) {
        sender.sendLogMessage("From EV3 : " + message);
    }

    @Override
    public void notifyNewMCLData(MCLData data) {
        sender.sendTransmittable(TransmittableType.MCL_DATA, data);
    }
}
