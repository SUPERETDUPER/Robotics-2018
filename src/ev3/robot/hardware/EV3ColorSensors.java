/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import common.Config;
import common.mapping.SurfaceMap;
import ev3.localization.RobotPoseProvider;
import ev3.robot.ColorSensors;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.Nullable;

/**
 * Static class allowing access to color sensors
 */
public final class EV3ColorSensors implements ColorSensors {
    @Nullable
    private static final EV3ColorSensor surfaceColorSensor;

    static {
        if (Config.currentMode == Config.Mode.SIM) {
            surfaceColorSensor = null;
        } else {
            surfaceColorSensor = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE);
        }
    }

    public static int getSurfaceColor() {
        if (surfaceColorSensor == null) {
            Pose currentPose = RobotPoseProvider.get().getPose();
            return SurfaceMap.getColorAtPoint((int) currentPose.getX(), (int) currentPose.getY());
        } else {
            return surfaceColorSensor.getColorID();
        }
    }

    @Override
    public int getColorSurfaceLeft() {
        return 0;
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