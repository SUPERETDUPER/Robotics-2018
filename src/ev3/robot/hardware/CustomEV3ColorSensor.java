/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import common.logger.Logger;
import lejos.hardware.DeviceException;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

class CustomEV3ColorSensor {
    private static final String LOG_TAG = CustomEV3ColorSensor.class.getSimpleName();

    private volatile SampleProvider sampleProvider;
    private float[] sample;
    private Thread setupThread;

    private final Port port;
    private EV3ColorSensor colorSensor;

    CustomEV3ColorSensor(Port port) {
        this.port = port;
    }

    void setup() {
        setupThread = new Thread() {
            @Override
            public synchronized void run() {
                synchronized (CustomEV3ColorSensor.this) {
                    if (colorSensor == null) {
                        try {
                            colorSensor = new EV3ColorSensor(port);
                        } catch (IllegalArgumentException | DeviceException e) {
                            Logger.warning(LOG_TAG, "Could not create color sensor at port " + port.toString());
                        }
                    }
                }
            }
        };

        setupThread.start();
    }

    boolean isSetup() {
        return !setupThread.isAlive();
    }

    int getColor() {
        if (colorSensor == null) {
            createSensor();
        }

        if (sampleProvider == null) {
            sampleProvider = colorSensor.getColorIDMode();
            sample = new float[sampleProvider.sampleSize()];
        }

        sampleProvider.fetchSample(sample, 0);
        return (int) sample[0];
    }

    float getRed() {
        if (colorSensor == null) {
            createSensor();
        }

        if (sampleProvider == null) {
            sampleProvider = colorSensor.getRedMode();
            sample = new float[sampleProvider.sampleSize()];
        }

        sampleProvider.fetchSample(sample, 0);
        return sample[0];
    }

    private synchronized void createSensor() {
        if (colorSensor == null) {
            colorSensor = new EV3ColorSensor(port);
        }
    }
}
