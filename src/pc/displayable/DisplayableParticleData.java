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

/**
 * Object that gets sent from the ev3 to the computer common.gui containing the particles particles and the currentPosition
 */
public class DisplayableParticleData extends ParticleAndPoseContainer implements Displayable {
    private static final String LOG_TAG = DisplayableParticleData.class.getSimpleName();

    private static final int PARTICLE_DIAMETER = 4;

    public DisplayableParticleData(Particle[] particles, Pose currentPose) {
        super(particles, currentPose);
    }

    @Override
    public synchronized void displayOnGui(@NotNull GraphicsContext g) {
        if (particles != null) {
            g.setFill(Color.BLUE);

            for (Particle particle : particles) {
                g.setFill(Color.rgb((int) (particle.weight * 255), (int) (255 - (particle.weight * 255)), 0));
                displayPoseOnGui(particle.getPose(), g);
            }
        }

        if (currentPose != null) {
            g.setFill(Color.BLUE);
            displayPoseOnGui(currentPose, g);
        }
    }

    private static void displayPoseOnGui(@NotNull Pose particlePose, @NotNull GraphicsContext g) {
        g.fillOval(particlePose.getX(), particlePose.getY(), PARTICLE_DIAMETER, PARTICLE_DIAMETER);

        if (particlePose.getY() < 0) {
            Logger.warning(LOG_TAG, particlePose.toString());
            throw new RuntimeException();
        }
    }

    @Override
    public boolean invert() {
        return true;
    }
}