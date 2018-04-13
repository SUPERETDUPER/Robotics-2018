/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import common.logger.Logger;
import ev3.communication.ComManager;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Controller implements MoveListener, NavigationListener {
    private static final String LOG_TAG = Controller.class.getSimpleName();

    private final MyNavigator navigator;

    public Controller(@NotNull MyNavigator navigator) {
        this.navigator = navigator;

        this.navigator.addNavigationListener(this);
        this.navigator.getMoveController().addMoveListener(this);
    }

    public void followPath(@NotNull Path path, Offset offset) {
        for (int i = 0; i < path.size(); i++) {
            Waypoint waypoint = path.get(i);

            Point newPoint = offset.reverseOffset(waypoint.getPose());

            if (waypoint.isHeadingRequired()) {
                path.set(i, new Waypoint(newPoint.x, newPoint.y, normalize(waypoint.getHeading())));
            } else {
                path.set(i, new Waypoint(newPoint.x, newPoint.y));
            }
        }

        navigator.followPath(path);

        ComManager comManager = ComManager.get();
        if (comManager != null) {
            comManager.sendTransmittable(navigator.getPath());
        }

        waitForStop();
    }

    public void followPath(@NotNull Path path) {
        followPath(path, new Offset(0, 0));
    }

    private void waitForStop() {
        while (navigator.isMoving()) {
            ComManager comManager = ComManager.get();
            if (comManager != null) {
                comManager.sendTransmittable(navigator.getPoseProvider().getPose());
            }

            Thread.yield();
        }
    }


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