/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.ColorSensors;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * Static class allowing access to color sensors
 */
public final class EV3ColorSensors implements ColorSensors {
    private static final EV3ColorSensor surfaceColorSensorLeft = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_LEFT);

    @Override
    public int getColorSurfaceLeft() {
        return surfaceColorSensorLeft.getColorID();
    }

    @Override
    public int getColorSurfaceRight() {
        return 0;
    }

    @Override
    public int getColorContainer() {
        return 0;
    }

    @Override
    public int getColorBoat() {
        return 0;
    }
}