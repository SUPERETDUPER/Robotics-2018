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

    private final float deltaX;
    private final float deltaY;

    public Offset(float deltaX, float deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    @NotNull
    public Point offset(@NotNull Pose pose) {
        return offset(pose, deltaX, deltaY);
    }

    @NotNull
    public Point reverseOffset(@NotNull Pose pose) {
        return offset(pose, -deltaX, -deltaY);
    }

    @NotNull
    private static Point offset(@NotNull Pose pose, float deltaX, float deltaY) {
        double originalTheta = Math.atan(deltaY / deltaX);

        //To fix problem with CAST rule quad 2
        if (deltaY >= 0 && originalTheta < 0) {
            originalTheta += Math.PI;
        } else if (deltaY < 0 && originalTheta >= 0){ //quad 3
            originalTheta += Math.PI;
        }

        double hypotenuse = Math.sqrt(deltaX * deltaX + deltaY * deltaY); //Pythagorean theorem

        double newTheta = originalTheta + Math.toRadians(pose.getHeading());

        float newXOffset = (float) (Math.cos(newTheta) * hypotenuse);
        float newYOffset = (float) (Math.sin(newTheta) * hypotenuse);

        return new Point(pose.getX() + newXOffset, pose.getY() + newYOffset);
    }
}
