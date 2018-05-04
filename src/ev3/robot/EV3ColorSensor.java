/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;

class EV3ColorSensor implements MotorSensor {
    private static final String LOG_TAG = EV3ColorSensor.class.getSimpleName();

    private volatile SampleProvider sampleProvider;
    private float[] sample;

    private final Port port;
    private lejos.hardware.sensor.EV3ColorSensor colorSensor;

    EV3ColorSensor(Port port) {
        this.port = port;
    }

    @Override
    public void create() {
        colorSensor = new lejos.hardware.sensor.EV3ColorSensor(port);
    }

    @Override
    public boolean isNotCreated() {
        return colorSensor == null;
    }

    int getColor() {
        if (sampleProvider == null) {
            sampleProvider = colorSensor.getColorIDMode();
            sample = new float[sampleProvider.sampleSize()];
        }

        sampleProvider.fetchSample(sample, 0); //Sample provider puts reading inside sample array
        return (int) sample[0]; //Returns the value in the sample array
    }

    float getRed() {
        if (sampleProvider == null) {
            sampleProvider = colorSensor.getRedMode();
            sample = new float[sampleProvider.sampleSize()];
        }

        sampleProvider.fetchSample(sample, 0);
        return sample[0];
    }
}
