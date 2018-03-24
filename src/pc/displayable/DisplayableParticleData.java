/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.displayable;

import common.Logger;
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

    private static final int PARTICLE_DIAMETER = 4;

    private ParticleAndPoseContainer data = new ParticleAndPoseContainer(null, null);

    public synchronized void displayOnGui(@NotNull GraphicsContext g) {
        if (data.getParticles() != null) {
            g.setFill(Color.BLUE);

            for (Particle particle : data.getParticles()) {
                g.setFill(Color.rgb((int) (particle.weight * 255), (int) (255 - (particle.weight * 255)), 0));
                displayPoseOnGui(particle.getPose(), g);
            }
        }

        if (data.getCurrentPose() != null) {
            g.setFill(Color.BLUE);
            displayPoseOnGui(data.getCurrentPose(), g);
        }
    }

    private static void displayPoseOnGui(@NotNull Pose particlePose, @NotNull GraphicsContext g) {
        g.fillOval(particlePose.getX(), particlePose.getY(), PARTICLE_DIAMETER, PARTICLE_DIAMETER);

        if (particlePose.getY() < 0) {
            Logger.warning(LOG_TAG, particlePose.toString());
            throw new RuntimeException();
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