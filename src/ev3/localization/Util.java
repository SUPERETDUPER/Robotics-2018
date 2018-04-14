/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.particles.Particle;
import ev3.navigation.Readings;
import lejos.robotics.geometry.Rectangle;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Util classes containing the core of the particle algorithm
 * TODO Review for bugs
 */
final class Util {
    private static final Random random = new Random();

    //How much the particles should spread
    private static final float DISTANCE_NOISE_FACTOR = 0.08F;
    private static final float ANGLE_NOISE_FACTOR = 0.4F;

    //How much the particles should be spread out at start
    private static final float STARTING_RADIUS_NOISE = 100;
    private static final float STARTING_HEADING_NOISE = 30;

    @Contract(pure = true)
    @NotNull
    static Pose movePose(@NotNull Pose pose, @NotNull Move move) {
        return movePose(pose, move, 0, 0);
    }

    @Contract(pure = true)
    static Particle[] moveParticleSet(@NotNull Particle[] particles, @NotNull Move move) {
        Particle[] newParticles = new Particle[particles.length];

        for (int i = 0; i < particles.length; i++) {
            Pose newPose = Util.movePose(particles[i].getPose(), move, ANGLE_NOISE_FACTOR, DISTANCE_NOISE_FACTOR);
            newParticles[i] = new Particle(newPose, particles[i].weight);
        }

        return newParticles;
    }

    /**
     * Subtracts move2 from move1
     * <p>
     * If move2 is null simply returns move one.
     *
     * @param move1 larger move
     * @param move2 smaller move
     * @return result of subtraction
     */
    @Contract(pure = true)
    @NotNull
    static Move subtractMove(@NotNull Move move1, @Nullable Move move2) {
        if (move2 == null) {
            return move1;
        }

        return new Move(move1.getMoveType(), move1.getDistanceTraveled() - move2.getDistanceTraveled(), move1.getAngleTurned() - move2.getAngleTurned(), move1.isMoving());
    }

    /**
     * THE ALGORITHM !!
     *
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
     * 2. For that sampled particle, shift it based on the move
     * 3. Calculate the probability of getting that reading from the shifted pose. That's the new weight
     * 4. Normalize weights (multiply all the weight by a constant so that the sum of the weights is one).
     *
     * @param particles particles to update
     * @param move      amount particles have moved
     * @param readings  readings the sensors took
     * @return updated particles
     */
    @Contract(pure = true)
    static Particle[] update(@NotNull Particle[] particles, Move move, Readings readings) {
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

            Pose newPose = movePose(particles[index].getPose(), move, ANGLE_NOISE_FACTOR, DISTANCE_NOISE_FACTOR);

            float newWeight = readings.calculateWeight(newPose);
            totalForNewWeights += newWeight;

            newParticles[spokeCounter] = new Particle(newPose, newWeight);
        }

        newParticles = normalize(newParticles, totalForNewWeights); //Normalize

        return newParticles;
    }

    /**
     * Generates a new particle set around a specific point with weights 0.5
     */
    @SuppressWarnings("SameParameterValue")
    @Contract(pure = true)
    @NotNull
    static Particle[] createNewParticleSet(Rectangle boundingRectangle, @NotNull Pose centerPose, int numParticles) {
        Particle[] newParticles = new Particle[numParticles];

        float totalWeight = 0;

        for (int i = 0; i < numParticles; i++) {
            float randomFactorDistance;
            float x;
            float y;

            do {
                randomFactorDistance = (float) random.nextGaussian() / 2;


                float radiusFromCenter = STARTING_RADIUS_NOISE * randomFactorDistance;

                float thetaInRad = (float) (2 * Math.PI * Math.random());  //Random angle between 0 and 2pi

                x = centerPose.getX() + radiusFromCenter * (float) Math.cos(thetaInRad);
                y = centerPose.getY() + radiusFromCenter * (float) Math.sin(thetaInRad);
            } while (!boundingRectangle.contains((int) x, (int) y));

            float randomFactorAngle = (float) random.nextGaussian() / 2;

            float heading = centerPose.getHeading() + STARTING_HEADING_NOISE * randomFactorDistance;

            float totalError = Math.abs(randomFactorDistance) + Math.abs(randomFactorAngle);
            float newWeight = bellCurveFunction(totalError);

            totalWeight += newWeight;

            newParticles[i] = new Particle(x, y, heading, newWeight);
        }

        newParticles = normalize(newParticles, totalWeight);

        return newParticles;
    }

    /**
     * Estimate currentPose from weighted average of the particles
     * Calculate statistics
     *
     * @param particles particles to use
     */
    @NotNull
    @Contract(pure = true)
    static synchronized Pose refineCurrentPose(@NotNull Particle[] particles) {
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

        // Normalize angle
        while (estimatedAngle > 360) estimatedAngle -= 360;
        while (estimatedAngle <= 0) estimatedAngle += 360;

        return new Pose(estimatedX, estimatedY, estimatedAngle);
    }

    @Contract(pure = true)
    @NotNull
    static Move deepCopyMove(@NotNull Move move) {
        return new Move(move.getMoveType(), move.getDistanceTraveled(), move.getAngleTurned(), move.getTravelSpeed(), move.getRotateSpeed(), move.isMoving());
    }

    /**
     * Returns f(x) for a standard bell curve with standard deviation 1, mean 0 and max 1
     *
     * @param x x
     * @return f(x)
     */
    @Contract(pure = true)
    private static float bellCurveFunction(float x) {
        return (float) Math.pow(Math.E, -Math.pow(x, 2) / 2);
    }

    /**
     * Makes the sum of the weights of all the particles equal to one
     */
    @Contract(pure = true)
    private static Particle[] normalize(@NotNull Particle[] particles, float totalWeight) {
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(particles[i], particles[i].weight / totalWeight);
        }

        return particles;
    }

    /**
     * Shifts a pose and applies noise
     */
    @Contract(pure = true)
    @NotNull
    private static Pose movePose(@NotNull Pose originalPose, @NotNull Move move, float angleNoiseFactor, float distanceNoiseFactor) {
        double angleInRad = Math.toRadians(originalPose.getHeading());

        double ym = move.getDistanceTraveled() * Math.sin(angleInRad);
        double xm = move.getDistanceTraveled() * Math.cos(angleInRad);

        return new Pose(
                (float) (originalPose.getX() + xm + distanceNoiseFactor * xm * random.nextGaussian()),
                (float) (originalPose.getY() + ym + distanceNoiseFactor * ym * random.nextGaussian()),
                (float) ((originalPose.getHeading() + move.getAngleTurned() + move.getAngleTurned() * angleNoiseFactor * random.nextGaussian()) % 360)
        );
    }
}