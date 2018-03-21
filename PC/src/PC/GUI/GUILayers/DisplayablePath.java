/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC.GUI.GUILayers;

import Common.Logger;
import PC.GUI.GUI;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.NotNull;

public final class DisplayablePath extends Path implements Displayable {
    private static final String LOG_TAG = DisplayablePath.class.getSimpleName();

    public void displayOnGui(@NotNull GraphicsContext g) {
        if (GUI.getCurrentPose() == null) {
            Logger.warning(LOG_TAG, "Could not display path, no current position");
            return;
        }

        Waypoint previous = new Waypoint(GUI.getCurrentPose());

        g.setStroke(Color.RED);

        for (Waypoint waypoint : this) {
            g.strokeLine((int) previous.x, (int) previous.y, (int) waypoint.x, (int) waypoint.y);
            previous = waypoint;
        }
    }
}