/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.particles.MCLData;
import common.particles.Particle;
import ev3.navigation.Readings;
import lejos.robotics.geometry.Rectangle;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

class ParticleSet extends MCLData {
    //How much the particles should spread
    private static final float DISTANCE_NOISE_FACTOR = 0.08F;
    private static final float ANGLE_NOISE_FACTOR = 0.4F;

    private final Rectangle boundingRectangle;

    ParticleSet(int numberOfParticles, Rectangle boundingRectngle, @NotNull Pose currentPose) {
        super(Util.createNewParticleSet(boundingRectngle, currentPose, numberOfParticles), currentPose);
        this.boundingRectangle = boundingRectngle;
    }

    void moveData(@NotNull Move move) {
        moveParticleSet(move);
        moveCurrentPose(move);
    }

    private void moveCurrentPose(Move move) {
        this.currentPose = Util.movePose(this.currentPose, move);
    }

    private void moveParticleSet(@NotNull Move move) {
        Particle[] newParticles = new Particle[particles.length];

        boolean needNormalizing = false;
        float totalWeight = 1;

        for (int i = 0; i < particles.length; i++) {
            Pose newPose = Util.movePose(particles[i].getPose(), move, ANGLE_NOISE_FACTOR, DISTANCE_NOISE_FACTOR);
            if (!boundingRectangle.contains(newPose.getLocation())) {
                newParticles[i] = new Particle(newPose, 0);
                totalWeight -= particles[i].weight;
                needNormalizing = true;
            } else {
                newParticles[i] = new Particle(newPose, particles[i].weight);
            }
        }

        if (needNormalizing) {
            this.particles = Util.normalizeSet(newParticles, totalWeight);
        } else {
            this.particles = newParticles;
        }
    }

    /**
     * THE ALGORITHM !!
     * <p>
     * The algorithm is as follows.
     * {@see https://classroom.udacity.com/courses/ud810/lessons/3353778638/concepts/33450785680923}
     * <p>
     * 1. Sample a random particle using a "spoke" algorithm.
     * Imagine a pie chart where each particle is a slice and the size of the slice is proportional to the particle's weight.
     * You then divide the pie chart into n equal sections with n "spokes". N is the number of particles
     * Where ever the spokes land, this is the new particle that has been sampled.
     * Particles with higher weights have higher changes of being chosen since they're bigger.
     * Also a dense cluster of particles have a higher chance of having one of their members being chosen since they together form a large section of the pie chart.
     * {@see https://classroom.udacity.com/courses/ud810/lessons/3353208568/concepts/33538586060923}
     * <p>
     * 2. For that sampled particle, shift it based on the moveData
     * 3. Calculate the probability of getting that reading from the shifted pose. That's the new weight
     * 4. Normalize weights (multiply all the weight by a constant so that the sum of the weights is one).
     *
     * @param readings readings the sensors took
     */
    @Contract(pure = true)
    void update(Readings readings) {
        Particle[] newParticles = new Particle[particles.length];

        double sizeOfSlice = 1.0 / particles.length;
        double offset = Math.random() * sizeOfSlice;
        double pastWeights = 0;
        float totalForNewWeights = 0;
        int index = 0;

        for (int spokeCounter = 0; spokeCounter < particles.length; spokeCounter++) {
            //Keep increasing index until we have found the particle that matches the spoke
            while (index != particles.length - 1 && pastWeights + particles[index].weight < offset + spokeCounter * sizeOfSlice) {
                pastWeights += particles[index++].weight; //Add weight of current particle to sum
            }


            /*Now index points to correct sampled particle*/

            float newWeight = readings.calculateWeight(particles[index].getPose());
            totalForNewWeights += newWeight;

            newParticles[spokeCounter] = new Particle(particles[index].getPose(), newWeight);
        }

        this.particles = Util.normalizeSet(newParticles, totalForNewWeights); //Normalize
    }

    /**
     * Estimate currentPose from weighted average of the particles
     * Calculate statistics
     */
    @Contract(pure = true)
    synchronized void refineCurrentPose() {
        float totalWeights = 0;

        float estimatedX = 0;
        float estimatedY = 0;
        float estimatedAngle = 0;

        for (Particle particle : particles) {
            estimatedX += particle.getPose().getX() * particle.weight;
            estimatedY += particle.getPose().getY() * particle.weight;
            estimatedAngle += particle.getPose().getHeading() * particle.weight;

            totalWeights += particle.weight;
        }

        estimatedX /= totalWeights;
        estimatedY /= totalWeights;
        estimatedAngle /= totalWeights;

        this.currentPose = new Pose(estimatedX, estimatedY, (float) Util.normalizeHeading(estimatedAngle));
    }
}
