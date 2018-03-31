/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class MapOperations {
    private static final float APPROACH_DIST = 250;

    private static final Point TEMP_REG_GREEN = new Point(1902, 306);
    private static final Point TEMP_REG_BLUE = new Point(1902, 838);
    private static final Point TEMP_REG_YELLOW = new Point(1578, 838);
    private static final Point TEMP_REG_RED = new Point(1578, 306);


    private static final Point CONTAINER_TOP_LEFT = new Point(871.5F, 756);
    private static final Point CONTAINER_TOP_RIGHT = new Point(1303.5F, 827);
    private static final Point CONTAINER_BOTTOM_LEFT = new Point(797.5F, 333);
    private static final Point CONTAINER_BOTTOM_RIGHT = new Point(1235.5F, 404);

    @Contract(pure = true)
    @NotNull
    public static Path getPathToContainerTopLeft(@NotNull Pose currentPose) {
        return approachLeftRight(CONTAINER_TOP_LEFT, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path getPathToContainerTopRight(@NotNull Pose currentPose) {
        return approachLeftRight(CONTAINER_TOP_RIGHT, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path goToContainerBottomLeft(@NotNull Pose currentPose) {
        return approachLeftRight(CONTAINER_BOTTOM_LEFT, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path goToContainerBottomRight(@NotNull Pose currentPose) {
        return approachLeftRight(CONTAINER_BOTTOM_RIGHT, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path goToTempRegGreen(@NotNull Pose currentPose) {
        return approachTopOrBottom(TEMP_REG_GREEN, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path goToTempRegBlue(@NotNull Pose currentPose) {
        return approachTopOrBottom(TEMP_REG_BLUE, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path goToTempRegRed(@NotNull Pose currentPose) {
        return approachTopOrBottom(TEMP_REG_RED, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path goToTempRegYellow(@NotNull Pose currentPose) {
        return approachTopOrBottom(TEMP_REG_YELLOW, currentPose);
    }

    @NotNull
    @Contract(pure = true)
    private static Path approachLeftRight(@NotNull Point point, @NotNull Pose currentPose) {
        Path path = goToClosest(currentPose, getApproachLeft(point), getApproachRight(point));

        path.add(new Waypoint(point));

        return path;
    }

    @NotNull
    @Contract(pure = true)
    private static Path approachTopOrBottom(@NotNull Point point, @NotNull Pose currentPose) {
        Path path = goToClosest(currentPose, getApproachTop(point), getApproachBottom(point));

        path.add(new Waypoint(point));

        return path;
    }

    @NotNull
    @Contract(pure = true)
    private static Path goToClosest(@NotNull Pose currentPose, @NotNull Pose option1, @NotNull Pose option2) {
        Path path = new Path();

        if (currentPose.distanceTo(option1.getLocation()) < currentPose.distanceTo(option2.getLocation())) {
            path.add(new Waypoint(option1));
        } else {
            path.add(new Waypoint(option2));
        }

        return path;
    }

    @Contract(pure = true)
    @NotNull
    private static Pose getApproachRight(@NotNull Point point) {
        return getApproachAt(point, 180);
    }

    @Contract(pure = true)
    @NotNull
    private static Pose getApproachTop(@NotNull Point point) {
        return getApproachAt(point, 270);
    }

    @Contract(pure = true)
    @NotNull
    private static Pose getApproachLeft(@NotNull Point point) {
        return getApproachAt(point, 0);
    }

    @Contract(pure = true)
    @NotNull
    private static Pose getApproachBottom(@NotNull Point point) {
        return getApproachAt(point, 90);
    }

    /**
     * Gets the pose you should be at to approach from angle
     *
     * @param point point to approach
     * @param angle angle to point when approaching
     * @return pose to approach point at angle
     */
    @NotNull
    @Contract(pure = true)
    private static Pose getApproachAt(@NotNull Point point, float angle) {
        Point approachPoint = point.pointAt(APPROACH_DIST, angle + 180);

        return new Pose(approachPoint.x, approachPoint.y, angle);
    }
}
