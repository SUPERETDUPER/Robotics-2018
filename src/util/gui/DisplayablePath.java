/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package util.gui;

import util.Logger;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class DisplayablePath extends Path implements Displayable {
    private static final String LOG_TAG = DisplayablePath.class.getSimpleName();

    private Pose currentPose;

    public void setCurrentPose(Pose currentPose) {
        this.currentPose = currentPose;
    }

    public void displayOnGui(@NotNull GraphicsContext g) {
        if (currentPose == null) {
            Logger.warning(LOG_TAG, "Could not display path, no current position");
            return;
        }

        Waypoint previousWaypoint = new Waypoint(currentPose);

        g.setStroke(Color.RED);

        for (Waypoint waypoint : this) {
            g.strokeLine((int) previousWaypoint.x, (int) previousWaypoint.y, (int) waypoint.x, (int) waypoint.y);
            previousWaypoint = waypoint;
        }
    }

    @Contract(pure = true)
    @Override
    public boolean invert() {
        return true;
    }
}