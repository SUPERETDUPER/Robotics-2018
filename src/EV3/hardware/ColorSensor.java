/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.hardware;

import Common.Config;
import Common.mapping.SurfaceMap;
import EV3.localization.ParticlePoseProvider;
import lejos.hardware.sensor.EV3ColorSensor;
import org.jetbrains.annotations.Nullable;

/**
 * Static class allowing access to color sensors
 */
public final class ColorSensor {
    @Nullable
    private static final EV3ColorSensor surfaceColorSensor;

    static {
        if (Config.useSimulator) {
            surfaceColorSensor = null;
        } else {
            surfaceColorSensor = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE);
        }
    }

    public static int getSurfaceColor() {
        if (surfaceColorSensor == null) {
            return SurfaceMap.get().getColorAtPoint(ParticlePoseProvider.get().getPose().getLocation());
        } else {
            return surfaceColorSensor.getColorID();
        }
    }
}