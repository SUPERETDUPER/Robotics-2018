/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.sim;

import Common.Config;
import lejos.utility.Delay;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AbstractMotorTest {
    @Test
    void main() {
        AbstractMotor motor = new AbstractMotor(null);

        motor.setSpeed(100);

        motor.forward();

        Delay.msDelay(2000);

        int tachoCount = motor.getTachoCount();

        Assertions.assertEquals(tachoCount, 200 / Config.SIM_SPEED_REDUCING_FACTOR);
    }
}