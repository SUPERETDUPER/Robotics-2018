/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import common.Config;
import common.logger.Logger;
import ev3.robot.Brick;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

import java.io.IOException;

public final class EV3Brick implements Brick {
    private static final String LOG_TAG = EV3Brick.class.getSimpleName();

    public void waitForUserConfirmation() {
        if (Config.currentMode == Config.Mode.SIM) {
            try {
                System.out.println("Press enter to continue");
                //noinspection ResultOfMethodCallIgnored
                System.in.read();
            } catch (IOException e) {
                Logger.error(LOG_TAG, e.toString());
            }
        } else {
            LCD.drawString("Press enter button to continue", 0, 0);
            Button.ENTER.waitForPress();
        }
    }
}