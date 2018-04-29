/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot;

import org.jetbrains.annotations.Contract;

/**
 * class allowing access to ev3's color sensors
 */
public final class EV3ColorSensors {
    private final CustomEV3ColorSensor surfaceLeft = new CustomEV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_LEFT);
    private final CustomEV3ColorSensor surfaceRight = new CustomEV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_RIGHT);
    private final CustomEV3ColorSensor container = new CustomEV3ColorSensor(Ports.PORT_SENSOR_COLOR_BLOCKS);
    private final CustomEV3ColorSensor boat = new CustomEV3ColorSensor(Ports.PORT_SENSOR_COLOR_BOAT);

    public void setup() {
        surfaceLeft.setup();
        surfaceRight.setup();
        container.setup();
        boat.setup();
    }

    @Contract(pure = true)
    public boolean isSetup() {
        return surfaceLeft.isSetup() && surfaceRight.isSetup() && container.isSetup() && boat.isSetup();
    }

    public float getColorSurfaceLeft() {
        return surfaceLeft.getRed();
    }

    public float getColorSurfaceRight() {
        return surfaceRight.getRed();
    }

    public int getColorContainer() {
        return container.getColor();
    }

    public int getColorBoat() {
        return boat.getColor();
    }
}