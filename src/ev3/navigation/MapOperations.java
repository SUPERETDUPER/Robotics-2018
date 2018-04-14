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
    private static final float APPROACH_DIST = 250; //How for to be when approaching a point

    //CONSTANTS FOR IMPORTANT POINTS
    private static final Waypoint TEMP_REG_GREEN = new Waypoint(1902, 306);
    private static final Waypoint TEMP_REG_BLUE = new Waypoint(1902, 838);
    private static final Waypoint TEMP_REG_YELLOW = new Waypoint(1578, 838);
    private static final Waypoint TEMP_REG_RED = new Waypoint(1578, 306);

    private static final Waypoint CONTAINER_TOP_LEFT = new Waypoint(871.5F, 756);
    private static final Waypoint CONTAINER_TOP_RIGHT = new Waypoint(1303.5F, 827);
    private static final Waypoint CONTAINER_BOTTOM_LEFT = new Waypoint(797.5F, 333);
    private static final Waypoint CONTAINER_BOTTOM_RIGHT = new Waypoint(1235.5F, 404);

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
    public static Path getPathToContainerBottomLeft(@NotNull Pose currentPose) {
        return approachLeftRight(CONTAINER_BOTTOM_LEFT, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path getPathToContainerBottomRight(@NotNull Pose currentPose) {
        return approachLeftRight(CONTAINER_BOTTOM_RIGHT, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path getPathToTempRegGreen(@NotNull Pose currentPose) {
        return approachTopOrBottom(TEMP_REG_GREEN, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path getPathToTempRegBlue(@NotNull Pose currentPose) {
        return approachTopOrBottom(TEMP_REG_BLUE, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path getPathToTempRegRed(@NotNull Pose currentPose) {
        return approachTopOrBottom(TEMP_REG_RED, currentPose);
    }

    @Contract(pure = true)
    @NotNull
    public static Path goToTempRegYellow(@NotNull Pose currentPose) {
        return approachTopOrBottom(TEMP_REG_YELLOW, currentPose);
    }

    /**
     * Finds the best path knowing it has to approach from the left or the right
     */
    @NotNull
    @Contract(pure = true)
    private static Path approachLeftRight(@NotNull Waypoint destination, @NotNull Pose currentPose) {
        Path path = new Path();
        path.add(getClosest(currentPose, getApproachLeft(destination), getApproachRight(destination)));
        path.add(destination);

        return path;
    }

    /**
     * Finds the best path knowing it has to approach from the left or the right
     */
    @NotNull
    @Contract(pure = true)
    private static Path approachTopOrBottom(@NotNull Waypoint destination, @NotNull Pose currentPose) {
        Path path = new Path();
        path.add(getClosest(currentPose, getApproachTop(destination), getApproachBottom(destination)));
        path.add(destination);

        return path;
    }

    @Contract(pure = true)
    @NotNull
    private static Waypoint getApproachRight(@NotNull Point point) {
        return getApproachAt(point, 180);
    }

    @Contract(pure = true)
    @NotNull
    private static Waypoint getApproachTop(@NotNull Point point) {
        return getApproachAt(point, 270);
    }

    @Contract(pure = true)
    @NotNull
    private static Waypoint getApproachLeft(@NotNull Point point) {
        return getApproachAt(point, 0);
    }

    @Contract(pure = true)
    @NotNull
    private static Waypoint getApproachBottom(@NotNull Point point) {
        return getApproachAt(point, 90);
    }

    /**
     * Gets the pose you should be at to approach from the specified angle
     *
     * @param point point to approach
     * @param angleOfApproach angle to point when approaching
     * @return pose to approach point at angle
     */
    @NotNull
    @Contract(pure = true)
    private static Waypoint getApproachAt(@NotNull Point point, float angleOfApproach) {
        Point approachPoint = point.pointAt(APPROACH_DIST, angleOfApproach + 180);

        return new Waypoint(approachPoint.x, approachPoint.y, angleOfApproach);
    }

    /**
     * Returns the pose that is closest to your current pose
     */
    @NotNull
    @Contract(pure = true)
    private static Waypoint getClosest(@NotNull Pose currentPose, @NotNull Waypoint option1, @NotNull Waypoint option2) {
        if (currentPose.distanceTo(option1.getPose().getLocation()) < currentPose.distanceTo(option2.getPose().getLocation())) {
            return option1;
        } else {
            return option2;
        }
    }
}
