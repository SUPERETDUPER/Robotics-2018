package Robotics2018.utils;

import Robotics2018.Config;
import Robotics2018.PC.Connection;
import lejos.hardware.Button;

import java.io.IOException;

public class Helper {
    private static final String LOG_TAG = Helper.class.getSimpleName();

    public static void waitForConfirmation() {
        if (Connection.isEV3 && !Config.isSimulator) {
            Button.ENTER.waitForPress();
        } else {
            try {
                Logger.info(LOG_TAG, "Press enter to continue");
                System.in.read();
            } catch (IOException e) {
                Logger.error(LOG_TAG, e.toString());
            }
        }
    }
}