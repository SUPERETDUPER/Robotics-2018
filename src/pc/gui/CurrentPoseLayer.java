/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Pose;

public class CurrentPoseLayer extends UpdatableLayer {
    private Pose pose;

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
}
