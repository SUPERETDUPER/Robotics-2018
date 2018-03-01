/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.navigation;

import Common.Logger;
import Common.mapping.SurfaceMap;
import EV3.Controller;
import EV3.hardware.ColorSensor;
import EV3.localization.EdgeReadings;
import EV3.localization.RobotPoseProvider;
import EV3.localization.SurfaceReadings;

/**
 * Check method checks if the color under the robot has changed. If so it calls the pose provider update method
 */
public final class LineChecker extends Thread {
    private static final String LOG_TAG = LineChecker.class.getSimpleName();

    private int previousColor;

    public LineChecker() {
        super();

        this.setDaemon(true);
        this.setName(LineChecker.class.getSimpleName());

        this.previousColor = ColorSensor.getSurfaceColor();
    }

    @Override
    public void run() {
        while(true) {
            //SurfaceReadings
            int surfaceColor = ColorSensor.getSurfaceColor();

            if (SurfaceMap.get().getColorAtPoint(RobotPoseProvider.get().getPose().getLocation()) != surfaceColor) {
                RobotPoseProvider.get().update(new SurfaceReadings(surfaceColor));
            }

            //EdgeReadings
            int currentColor = ColorSensor.getSurfaceColor();

            if (previousColor != currentColor) {
                Logger.info(LOG_TAG, "Changed zone " + previousColor + " to " + currentColor);
                Controller.get().update(new EdgeReadings(previousColor, currentColor));
                previousColor = currentColor;
            }
        }
    }
}
