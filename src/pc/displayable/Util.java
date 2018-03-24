/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.displayable;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;

class Util {

    private static final int LENGTH_OF_TAIL = 5;
    private static final int RADIUS = 2;

    static void displayPoseOnGui(GraphicsContext g, Pose pose) {
        g.fillOval(pose.getX() - RADIUS, pose.getY() - RADIUS, RADIUS * 2, RADIUS * 2);

        Point point = pose.pointAt(LENGTH_OF_TAIL, pose.getHeading() + 180);

        g.setStroke(Color.BLACK);
        g.strokeLine(pose.getX(), pose.getY(), point.x, point.y);
    }
}
