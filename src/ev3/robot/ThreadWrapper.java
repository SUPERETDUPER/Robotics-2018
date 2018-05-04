/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import common.logger.Logger;
import lejos.hardware.DeviceException;

class ThreadWrapper {
    private static final String LOG_TAG = ThreadWrapper.class.getSimpleName();

    private final MotorSensor motorSensor;

    private Thread creatorThread;

    ThreadWrapper(MotorSensor motorSensor) {
        this.motorSensor = motorSensor;
    }

    void setup() {
        creatorThread = new Thread() {
            @Override
            public synchronized void run() {
                if (!motorSensor.isCreated()) {
                    try {
                        motorSensor.create();
                    } catch (IllegalArgumentException | DeviceException e) {
                        Logger.warning(LOG_TAG, "Could not create sensor/motor");
                    }
                }
            }
        };

        creatorThread.start();
    }

    boolean isSetupAlive() {
        return creatorThread.isAlive();
    }

    MotorSensor get() {
        if (!motorSensor.isCreated()) {
            synchronized (this) {
                if (!motorSensor.isCreated()) {
                    motorSensor.create();
                }
            }
        }

        return motorSensor;
    }
}
