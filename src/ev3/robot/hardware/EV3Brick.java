/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.Robot;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public final class EV3Brick implements Robot.Brick {

    public void waitForUserConfirmation() {
        LCD.drawString("Press enter button to continue", 0, 0);
        Button.ENTER.waitForPress();
    }

    public void beep() {
        Sound.beep();
    }

    public void buzz() {
        Sound.buzz();
    }
}