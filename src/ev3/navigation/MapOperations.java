/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import ev3.localization.RobotPoseProvider;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import org.jetbrains.annotations.NotNull;

public final class MapOperations {
    private static final float APPROACH_DIST = 100;

    private static final Point TEMP_REG_GREEN = new Point(1902, 306);
    private static final Point TEMP_REG_BLUE = new Point(1902, 838);
    private static final Point TEMP_REG_YELLOW = new Point(1578, 838);
    private static final Point TEMP_REG_RED = new Point(1578, 306);


    public static void goToTempRegGreen() {
        approachTopOrBottom(TEMP_REG_GREEN);
    }

    public static void goToTempRegBlue() {
        approachTopOrBottom(TEMP_REG_BLUE);
    }

    public static void goToTempRegRed() {
        approachTopOrBottom(TEMP_REG_RED);
    }

    public static void goToTempRegYellow() {
        approachTopOrBottom(TEMP_REG_YELLOW);
    }

    private static void approachLeftRight(@NotNull Point point) {
        Pose currentPose = RobotPoseProvider.get().getPose();

        Pose option1 = getApproachLeft(point);
        Pose option2 = getApproachRight(point);

        if (currentPose.distanceTo(option1.getLocation()) < currentPose.distanceTo(option2.getLocation())) {
            Controller.get().getNavigator().addWaypoint(new Waypoint(option1));
        } else {
            Controller.get().getNavigator().addWaypoint(new Waypoint(option2));
        }

        Controller.get().getNavigator().addWaypoint(new Waypoint(point));
    }

    private static void approachTopOrBottom(@NotNull Point point) {
        Pose currentPose = RobotPoseProvider.get().getPose();

        Pose option1 = getApproachTop(point);
        Pose option2 = getApproachBottom(point);

        if (currentPose.distanceTo(option1.getLocation()) < currentPose.distanceTo(option2.getLocation())) {
            Controller.get().getNavigator().addWaypoint(new Waypoint(option1));
        } else {
            Controller.get().getNavigator().addWaypoint(new Waypoint(option2));
        }

        Controller.get().getNavigator().addWaypoint(new Waypoint(point));
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
