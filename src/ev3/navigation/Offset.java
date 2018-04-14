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
    public static final Offset LEFT_COLOR_SENSOR = new Offset(-171, 80);
    public static final Offset RIGHT_COLOR_SENSOR = new Offset(-133, -79);
    public static final Offset CENTER_OF_ROBOT = new Offset(-50, 0); //TODO Find actual values

    private final float deltaX;
    private final float deltaY;

    public Offset(float deltaX, float deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    @NotNull
    public Point offset(@NotNull Pose pose) {
        return calculateOffset(pose, deltaX, deltaY);
    }

    @NotNull
    public Point reverseOffset(@NotNull Pose pose) {
        return calculateOffset(pose, -deltaX, -deltaY);
    }

    @NotNull
    private static Point calculateOffset(@NotNull Pose pose, float deltaX, float deltaY) {
        double hypotenuse = Math.sqrt(deltaX * deltaX + deltaY * deltaY); //Pythagorean theorem

        double newTheta = Math.atan2(deltaY, deltaX) + Math.toRadians(pose.getHeading()); //Find angle formed by offset and add pose's heading

        float newXOffset = (float) (Math.cos(newTheta) * hypotenuse);
        float newYOffset = (float) (Math.sin(newTheta) * hypotenuse);

        return new Point(pose.getX() + newXOffset, pose.getY() + newYOffset);
    }
}
