/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.Logger;
import common.particles.Particle;
import common.particles.ParticleAndPoseContainer;
import ev3.navigation.Readings;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Used by the Pose Provider to run operations on its set of particles
 */
class MCLData extends ParticleAndPoseContainer {
    private static final String LOG_TAG = MCLData.class.getSimpleName();

    private static final float STARTING_RADIUS_NOISE = 4;
    private static final float STARTING_HEADING_NOISE = 3;

    private static final float DISTANCE_NOISE_FACTOR = 0.008F;
    private static final float ANGLE_NOISE_FACTOR = 0.04F;

    private static final int NUM_PARTICLES = 200;
    private static final int MAX_RESAMPLE_ITERATIONS = 1000;

    private static final float VALUE_OF_PREVIOUS_WEIGHT = 0.1F;

    private static final Random random = new Random();

    MCLData(@NotNull Pose startingPose) {
        super(MCLData.getNewParticleSet(startingPose), startingPose);
        currentPose = startingPose;
    }

    synchronized void weightParticles(@NotNull Readings readings) {
        Particle[] newParticles = new Particle[NUM_PARTICLES];

        for (int i = 0; i < NUM_PARTICLES; i++) {
            float readingWeight = readings.calculateWeight(particles[i].getPose());
            float newWeight = VALUE_OF_PREVIOUS_WEIGHT * particles[i].weight + (1 - VALUE_OF_PREVIOUS_WEIGHT) * readingWeight;
            newParticles[i] = new Particle(particles[i].getPose(), newWeight);
        }

        particles = newParticles;
    }

    synchronized void resample() {
        Particle[] newParticles = new Particle[MCLData.NUM_PARTICLES];

        int counter = 0;

        for (int x = 0; x < MAX_RESAMPLE_ITERATIONS; x++) {

            //Copy particles with weight higher than random
            for (int i = 0; i < NUM_PARTICLES; i++) {
                if (particles[i].weight >= Math.random()) {

                    newParticles[counter] = particles[i];
                    counter++;

                    if (counter == MCLData.NUM_PARTICLES) {
                        break;
                    }
                }
            }

            if (counter == MCLData.NUM_PARTICLES) {
                break;
            }
        }

        if (counter == 0) {
            Logger.error(LOG_TAG, "Bad resample ; totally lost");
        } else if (counter < MCLData.NUM_PARTICLES) {
            for (int i = counter; i < MCLData.NUM_PARTICLES; i++) {
                newParticles[i] = newParticles[i % counter];
            }

            Logger.warning(LOG_TAG, "Bad resample; had to duplicate existing particles");
        }

        particles = newParticles;
    }

    /**
     * Estimate currentPose from weighted average of the particles
     * Calculate statistics
     */
    synchronized void refineCurrentPose() {
        float totalWeights = 0;

        float estimatedX = 0;
        float estimatedY = 0;
        float estimatedAngle = 0;

        for (Particle particle : particles) {
            estimatedX += (particle.getPose().getX() * particle.weight);
            estimatedY += (particle.getPose().getY() * particle.weight);
            estimatedAngle += (particle.getPose().getHeading() * particle.weight);

            totalWeights += particle.weight;
        }

        estimatedX /= totalWeights;
        estimatedY /= totalWeights;
        estimatedAngle /= totalWeights;

        // Normalize angle
        while (estimatedAngle > 180) estimatedAngle -= 360;
        while (estimatedAngle < -180) estimatedAngle += 360;

        currentPose = new Pose(estimatedX, estimatedY, estimatedAngle);
    }

    synchronized void moveParticlesAndPose(Move move) {
        currentPose = Util.movePose(currentPose, move);
        particles = Util.moveParticleSet(particles, move, ANGLE_NOISE_FACTOR, DISTANCE_NOISE_FACTOR);
    }

    /**
     * Generates a new particle set around a specific point with weights 0.5
     */
    @NotNull
    private static Particle[] getNewParticleSet(@NotNull Pose centerPose) {
        Particle[] newParticles = new Particle[NUM_PARTICLES];

        for (int i = 0; i < NUM_PARTICLES; i++) {
            float radiusFromCenter = STARTING_RADIUS_NOISE * (float) random.nextGaussian();

            float thetaInRad = (float) (2 * Math.PI * Math.random());  //Random angle between 0 and 2pi

            float x = centerPose.getX() + radiusFromCenter * (float) Math.cos(thetaInRad);
            float y = centerPose.getY() + radiusFromCenter * (float) Math.sin(thetaInRad);

            float heading = centerPose.getHeading() + STARTING_HEADING_NOISE * (float) random.nextGaussian();

            newParticles[i] = new Particle(x, y, heading, 0.5F);
        }

        return newParticles;
    }
}