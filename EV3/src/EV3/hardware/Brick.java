/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.hardware;

import Common.Config;
import Common.Logger;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

import java.io.IOException;

public final class Brick {
    private static final String LOG_TAG = Brick.class.getSimpleName();

    public static void waitForUserConfirmation() {
        if (!Config.useSimulator) {
            LCD.drawString("Press enter button to continue", 0, 0);
            Button.ENTER.waitForPress();
        } else {
            try {
                System.out.println("Press enter to continue");
                //noinspection ResultOfMethodCallIgnored
                System.in.read();
            } catch (IOException e) {
                Logger.error(LOG_TAG, e.toString());
            }
        }
    }
}