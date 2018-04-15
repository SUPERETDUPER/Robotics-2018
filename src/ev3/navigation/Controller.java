/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.TransmittableType;
import common.logger.Logger;
import ev3.communication.ComManager;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Acts as a bridge to the navigator and provides helpful methods for the robot.
 */
public final class Controller implements MoveListener, NavigationListener {
    private static final String LOG_TAG = Controller.class.getSimpleName();

    private final MyNavigator navigator;

    public Controller(@NotNull MyNavigator navigator) {
        this.navigator = navigator;

        this.navigator.addNavigationListener(this);
        this.navigator.getMoveController().addMoveListener(this);
    }

    public void followPath(@NotNull Path path, @Nullable Offset offset) {
        Path newPath = new Path();

        for (Waypoint waypoint : path) {
            Point newPoint;

            if (offset == null) {
                newPoint = waypoint.getPose().getLocation();
            } else {
                newPoint = offset.reverseOffset(waypoint.getPose());
            }

            if (waypoint.isHeadingRequired()) {
                newPath.add(new Waypoint(newPoint.x, newPoint.y, normalize(waypoint.getHeading())));
            } else {
                newPath.add(new Waypoint(newPoint.x, newPoint.y));
            }
        }

        navigator.followPath(newPath);

        ComManager.sendTransmittable(TransmittableType.PATH, navigator.getPath());

        waitForStop();
    }

    public void followPath(@NotNull Path path) {
        followPath(path, null);
    }

    private void waitForStop() {
        while (navigator.isMoving()) {
            ComManager.sendTransmittable(TransmittableType.CURRENT_POSE, navigator.getPoseProvider().getPose());

            Thread.yield();
        }
    }

    //TODO Consider removing and instead working directly with the Navigator
    @Contract(pure = true)
    private static double normalize(double heading) {
        while (heading > 180) heading -= 360;
        while (heading <= -180) heading += 360;
        return heading;
    }

    @Contract(pure = true)
    @NotNull
    public Pose getPose() {
        return navigator.getPoseProvider().getPose();
    }

    @Contract(pure = true)
    public MyNavigator getNavigator() {
        return navigator;
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Started : " + move.toString());
    }

    @Override
    public void moveStopped(Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Stopped : " + move.toString());
    }

    @Override
    public void atWaypoint(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "At waypoint : " + waypoint + " pose : " + pose.toString());
    }

    @Override
    public void pathComplete(Waypoint waypoint, Pose pose, int i) {
//        Logger.info(LOG_TAG, "Path complete : " + waypoint + " pose : " + pose.toString());
    }

    @Override
    public void pathInterrupted(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "Path interrupted : " + waypoint + " pose : " + pose.toString());
    }
}