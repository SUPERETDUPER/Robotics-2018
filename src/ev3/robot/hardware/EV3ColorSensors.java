/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import ev3.robot.Robot;
import org.jetbrains.annotations.Contract;

/**
 * class allowing access to ev3's color sensors
 */
public final class EV3ColorSensors implements Robot.ColorSensors {
    private final CustomEV3ColorSensor surfaceLeft = new CustomEV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_LEFT);
    private final CustomEV3ColorSensor surfaceRight = new CustomEV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_RIGHT);
    private final CustomEV3ColorSensor container = new CustomEV3ColorSensor(Ports.PORT_SENSOR_COLOR_BLOCKS);
    private final CustomEV3ColorSensor boat = new CustomEV3ColorSensor(Ports.PORT_SENSOR_COLOR_BOAT);

    @Override
    public void setup() {
        surfaceLeft.setup();
        surfaceRight.setup();
        container.setup();
        boat.setup();
    }

    @Contract(pure = true)
    @Override
    public boolean isSetup() {
        return surfaceLeft.isSetup() && surfaceRight.isSetup() && container.isSetup() && boat.isSetup();
    }

    @Override
    public float getColorSurfaceLeft() {
        return surfaceLeft.getRed();
    }

    @Override
    public float getColorSurfaceRight() {
        return surfaceRight.getRed();
    }

    @Override
    public int getColorContainer() {
        return container.getColor();
    }

    @Override
    public int getColorBoat() {
        return boat.getColor();
    }
}