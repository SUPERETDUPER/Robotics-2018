/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import ev3.navigation.Offset;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;

class CurrentPoseLayer extends UpdatableLayer {
    private Pose pose;

    CurrentPoseLayer(double width, double height) {
        super(width, height);
    }

    @Override
    void displayOnGui(GraphicsContext g) {
        if (pose != null) {
            g.setFill(Color.HOTPINK);
            Util.displayPoseOnGui(g, pose);

            g.setFill(Color.PURPLE);
            Point leftColorSensorPoint = Offset.LEFT_COLOR_SENSOR.offset(pose);
            Util.displayPoseOnGui(g, new Pose(leftColorSensorPoint.x, leftColorSensorPoint.y, pose.getHeading()));
            Point rightColorSensorPoint = Offset.RIGHT_COLOR_SENSOR.offset(pose);
            Util.displayPoseOnGui(g, new Pose(rightColorSensorPoint.x, rightColorSensorPoint.y, pose.getHeading()));
        }
    }

    @Override
    boolean shouldInvert() {
        return true;
    }

    @Override
    Transmittable getContent() {
        if (pose == null) {
            pose = new Pose();
        }

        return pose;
    }

    public Pose getPose() {
        return pose;
    }
}
