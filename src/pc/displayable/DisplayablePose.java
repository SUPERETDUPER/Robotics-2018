/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.displayable;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.navigation.Pose;

import java.io.DataInputStream;
import java.io.IOException;

public class DisplayablePose extends UpdatableLayer {
    private Pose pose = new Pose();

    @Override
    public void displayOnGui(GraphicsContext g) {
        g.setFill(Color.BLACK);
        Util.displayPoseOnGui(g, pose);
    }

    @Override
    public boolean invert() {
        return true;
    }

    @Override
    public synchronized void updateLayer(DataInputStream dataInputStream) throws IOException {
        pose.loadObject(dataInputStream);
    }
}
