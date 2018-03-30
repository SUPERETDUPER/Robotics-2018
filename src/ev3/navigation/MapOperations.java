/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

public final class MapOperations {
    private static final float APPROACH_DIST = 250;

    private static final Point TEMP_REG_GREEN = new Point(1902, 306);
    private static final Point TEMP_REG_BLUE = new Point(1902, 838);
    private static final Point TEMP_REG_YELLOW = new Point(1578, 838);
    private static final Point TEMP_REG_RED = new Point(1578, 306);
    private static final Point TEST = new Point(10,10);

    private static final Point CONTAINER_TOP_LEFT = new Point(871.5F, 756);
    private static final Point CONTAINER_TOP_RIGHT = new Point(1303.5F, 827);
    private static final Point CONTAINER_BOTTOM_LEFT = new Point(797.5F, 333);
    private static final Point CONTAINER_BOTTOM_RIGHT = new Point(1235.5F, 404);

    public static void goToContainerTopLeft(Pose currentPose) {
        approachLeftRight(CONTAINER_TOP_LEFT, currentPose);
    }

    public static void goToTest(Pose currentPose) {
        approachLeftRight(TEST, currentPose);
    }

    public static void goToContainerTopRight(Pose currentPose) {
        approachLeftRight(CONTAINER_TOP_RIGHT, currentPose);
    }

    public static void goToContainerBottomLeft(Pose currentPose) {
        approachLeftRight(CONTAINER_BOTTOM_LEFT, currentPose);
    }

    public static void goToContainerBottomRight(Pose currentPose) {
        approachLeftRight(CONTAINER_BOTTOM_RIGHT, currentPose);
    }

    public static void goToTempRegGreen(Pose currentPose) {
        approachTopOrBottom(TEMP_REG_GREEN, currentPose);
    }

    public static void goToTempRegBlue(Pose currentPose) {
        approachTopOrBottom(TEMP_REG_BLUE, currentPose);
    }

    public static void goToTempRegRed(Pose currentPose) {
        approachTopOrBottom(TEMP_REG_RED, currentPose);
    }

    public static void goToTempRegYellow(Pose currentPose) {
        approachTopOrBottom(TEMP_REG_YELLOW, currentPose);
    }

    private static void approachLeftRight(@NotNull Point point, Pose currentPose) {
        goToClosest(currentPose, getApproachLeft(point), getApproachRight(point));

        Controller.get().goTo(point);
    }

    private static void approachTopOrBottom(@NotNull Point point, Pose currentPose) {

        goToClosest(currentPose, getApproachTop(point), getApproachBottom(point));

        Controller.get().goTo(point);
    }

    private static void goToClosest(Pose currentPose, Pose option1, Pose option2) {
        if (currentPose.distanceTo(option1.getLocation()) < currentPose.distanceTo(option2.getLocation())) {
            Controller.get().goTo(option1);
        } else {
            Controller.get().goTo(option2);
        }
    }

    @NotNull
    private static Pose getApproachRight(@NotNull Point point) {
        return getApproachAt(point, 180);
    }

    @NotNull
    private static Pose getApproachTop(@NotNull Point point) {
        return getApproachAt(point, 270);
    }

    @NotNull
    private static Pose getApproachLeft(@NotNull Point point) {
        return getApproachAt(point, 0);
    }

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
    private static Pose getApproachAt(@NotNull Point point, float angle) {
        Point approachPoint = point.pointAt(APPROACH_DIST, angle + 180);

        return new Pose(approachPoint.x, approachPoint.y, angle);
    }
}
