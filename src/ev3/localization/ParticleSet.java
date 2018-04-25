/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.mapping.MapDataReader;
import common.particles.MCLData;
import common.particles.Particle;
import ev3.navigation.Readings;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

class ParticleSet extends MCLData {
    //How much the particles should spread
    private static final float DISTANCE_NOISE_FACTOR = 0.08F;
    private static final float ANGLE_NOISE_FACTOR = 0.4F;

    //How much the particles should be spread out at start
    private static final float STARTING_RADIUS_NOISE = 25;
    private static final float STARTING_HEADING_NOISE = 5;

    private final MapDataReader surfaceMap;

    ParticleSet(int numberOfParticles, MapDataReader surfaceMap, @NotNull Pose currentPose) {
        super(new Particle[numberOfParticles], currentPose);
        this.surfaceMap = surfaceMap;
        setPose(currentPose);
    }

    void setPose(@NotNull Pose pose){
        resetParticles(pose);
        this.currentPose = pose;
    }

    /**
     * Generates a new particle set around a specific point with weights 0.5
     */
    private void resetParticles(@NotNull Pose centerPose) {
        Particle[] newParticles = new Particle[particles.length];

        Random random = new Random();

        float totalWeight = 0;

        for (int i = 0; i < particles.length; i++) {
            float randomFactorDistance;
            float x;
            float y;

            //Create x,y values within bounds
            do {
                randomFactorDistance = (float) random.nextGaussian();

                float distanceFromCenter = STARTING_RADIUS_NOISE * randomFactorDistance;

                float thetaInRad = (float) (2 * Math.PI * Math.random());  //Random angle between 0 and 2pi

                x = centerPose.getX() + distanceFromCenter * (float) Math.cos(thetaInRad);
                y = centerPose.getY() + distanceFromCenter * (float) Math.sin(thetaInRad);
            } while (!surfaceMap.contains(new Point(x,y)));

            float randomFactorAngle = (float) random.nextGaussian();

            float heading = centerPose.getHeading() + STARTING_HEADING_NOISE * randomFactorAngle;

            float averageError = (Math.abs(randomFactorDistance) + Math.abs(randomFactorAngle)) / 2;
            float newWeight = Util.bellCurveFunction(averageError); //The closer the error to zero the closer the weight is to 1

            totalWeight += newWeight;

            newParticles[i] = new Particle(x, y, heading, newWeight);
        }

        particles = Util.normalizeSet(newParticles, totalWeight);
    }

    void moveData(@NotNull Move move) {
        moveParticleSet(move);
        moveCurrentPose(move);
    }

    void moveCurrentPose(Move move) {
        this.currentPose = Util.movePose(this.currentPose, move);
    }

    private void moveParticleSet(@NotNull Move move) {
        Particle[] newParticles = new Particle[particles.length];

        for (int i = 0; i < particles.length; i++) {
            newParticles[i] = new Particle(
                    Util.movePose(particles[i].getPose(), move, ANGLE_NOISE_FACTOR, DISTANCE_NOISE_FACTOR),
                    particles[i].weight
            );
        }

        this.particles = newParticles;
    }

    /**
     * THE ALGORITHM !!
     * <p>
     * The algorithm is as follows.
     * {@see https://classroom.udacity.com/courses/ud810/lessons/3353778638/concepts/33450785680923}
     * <p>
     * 1. Resample the particles using the "spoke" algorithm.
     * 2. For that sampled particle, shift it based on the move
     * 3. Calculate the probability of getting that reading from the shifted pose. That's the new weight
     * 4. Normalize weights (multiply all the weight by a constant so that the sum of the weights is one).
     *
     * @param readings readings the sensors took
     */
    @Contract(pure = true)
    void update(Move move, Readings readings) {
        resample();
        moveParticleSet(move);
        float totalWeight = reweight(readings);
        normalize(totalWeight);
    }

    /**
     * Imagine a pie chart where each particle is a slice and the size of the slice is proportional to the particle's weight.
     * You then divide the pie chart into n equal sections with n "spokes". N is the number of particles
     * Where ever the spokes land, this is the new particle that has been sampled.
     * Particles with higher weights have higher changes of being chosen since they're bigger.
     * Also a dense cluster of particles have a higher chance of having one of their members being chosen since they together form a large section of the pie chart.
     * {@see https://classroom.udacity.com/courses/ud810/lessons/3353208568/concepts/33538586060923}
     */
    private void resample() {
        Particle[] newParticles = new Particle[particles.length];

        double sizeOfSlice = 1.0 / particles.length;
        double offset = Math.random() * sizeOfSlice;
        double pastWeights = 0;
        int index = 0;

        for (int spokeCounter = 0; spokeCounter < particles.length; spokeCounter++) {
            //Keep increasing index until we have found the particle that matches the spoke
            while (index != particles.length - 1 && pastWeights + particles[index].weight < offset + spokeCounter * sizeOfSlice) {
                pastWeights += particles[index++].weight; //Add weight of current particle to sum
            }

            //Now index points to correct sampled particle
            newParticles[spokeCounter] = particles[index];
        }

        this.particles = newParticles; //Normalize
    }

    private float reweight(Readings readings) {
        Particle[] newParticles = new Particle[particles.length];

        float totalWeight = 0;

        for (int i = 0; i < particles.length; i++) {
            float newWeight = readings.calculateWeight(particles[i].getPose());
            totalWeight += newWeight;
            newParticles[i] = new Particle(particles[i].getPose(), newWeight);
        }

        this.particles = newParticles;

        return totalWeight;
    }

    private void normalize(float totalWeight) {
        this.particles = Util.normalizeSet(particles, totalWeight);
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
