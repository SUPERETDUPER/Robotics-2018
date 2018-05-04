/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

class EV3LargeMotor implements MotorSensor {
    private final static String LOG_TAG = EV3LargeMotor.class.getSimpleName();

    private RegulatedMotor regulatedMotor;
    private final Port port;

    @Override
    public void create() {
        regulatedMotor = new EV3LargeRegulatedMotor(port);
    }

    @Override
    public boolean isNotCreated() {
        return regulatedMotor == null;
    }

    EV3LargeMotor(Port port) {
        this.port = port;
    }

    RegulatedMotor get() {
        return regulatedMotor;
    }
}
