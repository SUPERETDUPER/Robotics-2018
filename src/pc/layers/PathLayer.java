/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.layers;

import common.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class PathLayer extends UpdatableLayer {
    private static final String LOG_TAG = PathLayer.class.getSimpleName();

    private Pose currentPose;
    private Path path = new Path();

    public void setCurrentPose(Pose currentPose) {
        this.currentPose = currentPose;
    }

    @Override
    void displayOnGui(@NotNull GraphicsContext g) {
        if (currentPose == null) {
            Logger.warning(LOG_TAG, "Could not display path, no current position");
            return;
        }

        Waypoint previousWaypoint = new Waypoint(currentPose);

        g.setStroke(Color.RED);

        for (Waypoint waypoint : path) {
            g.strokeLine((int) previousWaypoint.x, (int) previousWaypoint.y, (int) waypoint.x, (int) waypoint.y);
            previousWaypoint = waypoint;
        }
    }

    @Contract(pure = true)
    @Override
    boolean shouldInvert() {
        return true;
    }

    @Override
    Transmittable getContent() {
        return path;
    }
}