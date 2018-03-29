/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.Brick;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public final class EV3Brick implements Brick {
    private static final String LOG_TAG = EV3Brick.class.getSimpleName();

    public void waitForUserConfirmation() {
        LCD.drawString("Press enter button to continue", 0, 0);
        Button.ENTER.waitForPress();
    }
}