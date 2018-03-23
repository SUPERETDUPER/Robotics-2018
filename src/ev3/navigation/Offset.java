/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

/**
 * Responsible of offsetting readings
 */
public final class Offset {
    // (x,y) offsets when robot is facing to the right (heading = 0)
    private static final float[] RELATIVE_OFFSET_LEFT = {-1, -1};
    private static final float[] RELATIVE_OFFSET_RIGHT = {1, -1};

    @NotNull
    public static Point leftColorSensor(@NotNull Pose pose) {
        return offset(pose, RELATIVE_OFFSET_LEFT[0], RELATIVE_OFFSET_RIGHT[1]);
    }

    @NotNull
    public static Point rightColorSensor(@NotNull Pose pose) {
        return offset(pose, RELATIVE_OFFSET_RIGHT[0], RELATIVE_OFFSET_RIGHT[1]);
    }

    @NotNull
    private static Point offset(@NotNull Pose pose, float xOffset, float yOffset) {
        double originalTheta = Math.atan(yOffset / xOffset);
        double hypotenuse = Math.sqrt(xOffset * xOffset + yOffset * yOffset); //Pythagorean theorem

        double newTheta = originalTheta + Math.toRadians(pose.getHeading());

        float newXOffset = (float) (Math.cos(newTheta) * hypotenuse);
        float newYOffset = (float) (Math.sin(newTheta) * hypotenuse);

        return new Point(pose.getX() + newXOffset, pose.getY() + newYOffset);
    }
}
