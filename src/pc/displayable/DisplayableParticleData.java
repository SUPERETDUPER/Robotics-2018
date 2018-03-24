/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.displayable;

import common.particles.Particle;
import common.particles.ParticleAndPoseContainer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Object that gets sent from the ev3 to the computer common.gui containing the particles particles and the currentPosition
 */
public class DisplayableParticleData extends UpdatableLayer {
    private static final String LOG_TAG = DisplayableParticleData.class.getSimpleName();

    private ParticleAndPoseContainer data = new ParticleAndPoseContainer(null, null);

    public synchronized void displayOnGui(@NotNull GraphicsContext g) {
        if (data.getParticles() != null) {
            g.setFill(Color.BLUE);

            for (Particle particle : data.getParticles()) {
                g.setFill(Color.rgb((int) (particle.weight * 255), (int) (255 - (particle.weight * 255)), 0));
                Util.displayPoseOnGui(g, particle.getPose());
            }
        }

        if (data.getCurrentPose() != null) {
            g.setFill(Color.BLUE);
            Util.displayPoseOnGui(g, data.getCurrentPose());
        }
    }

    public Pose getCurrentPose() {
        return data.getCurrentPose();
    }

    public boolean invert() {
        return true;
    }

    @Override
    public synchronized void updateLayer(DataInputStream dataInputStream) throws IOException {
        data.loadObject(dataInputStream);
    }
}