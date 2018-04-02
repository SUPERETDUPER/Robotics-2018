/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Pose;

class CurrentPoseLayer extends UpdatableLayer {
    private Pose pose;

    public CurrentPoseLayer(int width, int height) {
        super(width, height);
    }

    @Override
    void displayOnGui(GraphicsContext g) {
        if (pose != null) {
            g.setFill(Color.BLACK);
            Util.displayPoseOnGui(g, pose);
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
