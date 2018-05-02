/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.Config;
import common.ConnectionUtil;
import common.logger.Logger;

final class PCMain {

    private static final String LOG_TAG = PCMain.class.getSimpleName();

    public static void main(final String[] args) {
        if (!Config.sendLogToPC) {
            Logger.error(LOG_TAG, "No PC required in mode solo");
            return;
        }

        final DataReceiver dataReceiver = new DataReceiver(
                ConnectionUtil.getInputStream(
                        ConnectionUtil.createClientSocket(
                                Config.PORT_TO_CONNECT_ON_EV3,
                                Config.EV3_IP_ADDRESS
                        )
                )
        );

        dataReceiver.read();
    }
}