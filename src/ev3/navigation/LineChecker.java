/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import ev3.hardware.ColorSensor;
import ev3.localization.RobotPoseProvider;
import ev3.localization.SurfaceReadings;
import lejos.utility.Delay;

/**
 * Check method checks if the color under the robot has changed. If so it calls the pose provider update method
 */
public final class LineChecker extends Thread {
    private static final String LOG_TAG = LineChecker.class.getSimpleName();

    LineChecker() {
        super();

        this.setDaemon(true);
        this.setName(LineChecker.class.getSimpleName());
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            RobotPoseProvider.get().update(new SurfaceReadings(ColorSensor.getSurfaceColor()));

            Delay.msDelay(100);
        }
    }
}
