/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.Config;
import common.ConnectionUtil;
import common.RunModes;
import common.logger.Logger;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import pc.gui.GUI;

public final class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(final String[] args) {
        if (Config.currentMode == RunModes.SOLO) {
            Logger.error(LOG_TAG, "No PC required in mode solo");
            return;
        }

        final DataReceiver dataReceiver = new DataReceiver(
                ConnectionUtil.getInputStream(
                        ConnectionUtil.createClientSocket(
                                Config.PORT_TO_CONNECT_ON_EV3,
                                Config.currentMode == RunModes.SIM ? "localhost" : Config.EV3_IP_ADDRESS
                        )
                ),
                GUI.listener
        );

        GUI.launchGUI(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        dataReceiver.stop();
                    }
                }
        );

        dataReceiver.read();
    }
}