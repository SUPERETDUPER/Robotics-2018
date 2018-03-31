/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.Config;
import common.TransmittableType;
import common.logger.LogMessage;
import common.logger.LogMessageListener;
import common.logger.Logger;
import common.particles.MCLData;
import ev3.localization.MCLDataListener;
import ev3.localization.RobotPoseProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataListener implements MCLDataListener, LogMessageListener{
    @NotNull
    private final DataSender sender;

    @Nullable
    private RobotPoseProvider robotPoseProvider;

    DataListener(@NotNull DataSender sender) {
        this.sender = sender;
    }

    public void attachToRobotPoseProvider(RobotPoseProvider robotPoseProvider) {
        this.robotPoseProvider = robotPoseProvider;
        this.robotPoseProvider.addListener(this);
    }

    void startListening() {
        //Attach the listener if not using the simulator so that log messages are sent
        if (Config.currentMode == Config.Mode.DUAL) {
            Logger.setListener(this);
        }
    }

    void stopListening() {
        Logger.removeListener();

        if (robotPoseProvider != null) {
            robotPoseProvider.removeListener(this);
        }
    }

    @Override
    public void notifyLogMessage(LogMessage message) {
        sender.sendTransmittable(TransmittableType.LOG, message);
    }

    @Override
    public void notifyNewMCLData(MCLData data) {
        sender.sendTransmittable(TransmittableType.MCL_DATA, data);
    }
}
