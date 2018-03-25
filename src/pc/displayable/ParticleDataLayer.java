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
public class ParticleDataLayer extends UpdatableLayer {
    private static final String LOG_TAG = ParticleDataLayer.class.getSimpleName();

    private ParticleAndPoseContainer data = new ParticleAndPoseContainer(null, null);

    @Override
    public synchronized void displayOnGui(@NotNull GraphicsContext g) {
        if (data.getParticles() != null) {
            Particle[] normalizedParticles = normalizeWeightTo255(data.getParticles());

            for (Particle particle : normalizedParticles) {
                g.setFill(Color.rgb((int) particle.weight, (int) (255 - particle.weight), 0));
                Util.displayPoseOnGui(g, particle.getPose());
            }
        }

        if (data.getCurrentPose() != null) {
            g.setFill(Color.BLUE);
            Util.displayPoseOnGui(g, data.getCurrentPose());
        }
    }

    /**
     * Multiples all the particle weights by a constant so that the highest weight reaches 255
     *
     * @param particles particles
     * @return normalized particles
     */
    private static Particle[] normalizeWeightTo255(@NotNull Particle[] particles) {
        float maxWeight = 0;

        for (Particle particle : particles) {
            if (particle.weight > maxWeight) {
                maxWeight = particle.weight;
            }
        }

        Particle[] newParticles = new Particle[particles.length];

        for (int i = 0; i < particles.length; i++) {
            newParticles[i] = particles[i].getParticleWithNewWeight(particles[i].weight * 255 / maxWeight);
        }

        return newParticles;
    }

    public Pose getCurrentPose() {
        return data.getCurrentPose();
    }

    @Override
    public boolean invert() {
        return true;
    }

    @Override
    public synchronized void updateLayer(DataInputStream dataInputStream) throws IOException {
        data.loadObject(dataInputStream);
    }
}