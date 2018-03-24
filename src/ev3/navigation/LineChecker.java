/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import ev3.hardware.ColorSensor;
import ev3.localization.EdgeReadings;
import ev3.localization.RobotPoseProvider;

/**
 * Check method checks if the color under the robot has changed. If so it calls the pose provider update method
 */
public final class LineChecker extends Thread {
    private static final String LOG_TAG = LineChecker.class.getSimpleName();

    private int previousColor;

    LineChecker() {
        super();

        this.setDaemon(true);
        this.setName(LineChecker.class.getSimpleName());

        this.previousColor = ColorSensor.getSurfaceColor();
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            int surfaceColor = ColorSensor.getSurfaceColor();

            if (previousColor != surfaceColor) {
                RobotPoseProvider.get().update(new EdgeReadings(previousColor, surfaceColor));
                previousColor = surfaceColor;
            }
        }
    }
}
