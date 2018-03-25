/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.displayable;

import common.Config;
import common.Logger;
import common.mapping.SurfaceMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

class Util {
    private static final String LOG_TAG = Util.class.getSimpleName();

    private static final int LENGTH_OF_TAIL = 5;
    private static final int RADIUS = 3;

    static void displayPoseOnGui(@NotNull GraphicsContext g, @NotNull Pose pose) {
        if (!SurfaceMap.contains((int) pose.getX(), (int) pose.getY())) {
            Logger.info(LOG_TAG, pose.toString());
            throw new RuntimeException();
        }


        g.fillOval(pose.getX() - RADIUS, pose.getY() - RADIUS, RADIUS * 2, RADIUS * 2);

        if (Config.SHOW_PARTICLE_TAILS) {
            Point point = pose.pointAt(LENGTH_OF_TAIL, pose.getHeading() + 180);

            g.setStroke(Color.BLACK);
            g.strokeLine(pose.getX(), pose.getY(), point.x, point.y);
        }
    }
}
