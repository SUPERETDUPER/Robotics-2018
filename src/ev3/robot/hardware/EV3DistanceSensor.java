/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.Robot;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class EV3DistanceSensor implements Robot.DistanceSensor {
    private final SampleProvider sensor = new EV3UltrasonicSensor(Ports.PORT_SENSOR_DISTANCE).getDistanceMode();
    private final float[] sample = new float[sensor.sampleSize()];

    @Override
    public float getDistance() {
        sensor.fetchSample(sample, 0);
        return sample[0];
    }
}