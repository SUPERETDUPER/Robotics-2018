/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import ev3.localization.RobotPoseProvider;
import ev3.localization.SurfaceReadings;
import ev3.robot.ColorSensors;
import lejos.utility.Delay;

/**
 * Check method checks if the color under the robot has changed. If so it calls the pose provider update method
 */
public final class LineChecker extends Thread {
    private static final String LOG_TAG = LineChecker.class.getSimpleName();

    private final ColorSensors colorSensors;

    LineChecker(ColorSensors colorSensors) {
        super();

        this.colorSensors = colorSensors;

        this.setDaemon(true);
        this.setName(LineChecker.class.getSimpleName());
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            RobotPoseProvider.get().update(new SurfaceReadings(colorSensors.getColorSurfaceLeft()));

            Delay.msDelay(100);
        }
    }
}
