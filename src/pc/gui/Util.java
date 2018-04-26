/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import common.Config;
import common.particles.Particle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

class Util {
    // --Commented out by Inspection (25/04/18 8:39 PM):private static final String LOG_TAG = Util.class.getSimpleName();

    private static final int LENGTH_OF_TAIL = 5;
    private static final int RADIUS = 3;

    static void displayPoseOnGui(@NotNull GraphicsContext g, @NotNull Pose pose) {
        g.fillOval(pose.getX() - RADIUS, pose.getY() - RADIUS, RADIUS * 2, RADIUS * 2);

        if (Config.SHOW_PARTICLE_TAILS) {
            Point point = pose.pointAt(LENGTH_OF_TAIL, pose.getHeading() + 180);

            g.setStroke(Color.BLACK);
            g.strokeLine(pose.getX(), pose.getY(), point.x, point.y);
        }
    }

    /**
     * Multiples all the particle weights by a constant so that the highest weight reaches 255
     *
     * @param unNormalizedParticles un-normalized particles
     * @return normalized normalized particles
     */
    static Particle[] normalizeWeightTo255(@NotNull Particle[] unNormalizedParticles) {
        float maxWeight = 0;

        //Find the max weight
        for (Particle particle : unNormalizedParticles) {
            if (particle.weight > maxWeight) {
                maxWeight = particle.weight;
            }
        }

        Particle[] normalizedParticles = new Particle[unNormalizedParticles.length];

        for (int i = 0; i < unNormalizedParticles.length; i++) {
            normalizedParticles[i] = new Particle(unNormalizedParticles[i], unNormalizedParticles[i].weight / maxWeight * 255);
        }

        return normalizedParticles;
    }
}
