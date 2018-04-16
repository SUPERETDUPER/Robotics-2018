/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.mapping.SurfaceMap;
import common.particles.Particle;
import lejos.robotics.geometry.Point;
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

    //How much the particles should be spread out at start
    private static final float STARTING_RADIUS_NOISE = 50;
    private static final float STARTING_HEADING_NOISE = 15;

    @Contract(pure = true)
    @NotNull
    static Pose movePose(@NotNull Pose pose, @NotNull Move move) {
        return movePose(pose, move, 0, 0);
    }

    /**
     * Subtracts move2 from move1
     * <p>
     * If move2 is null simply returns moveData one.
     *
     * @param move1 larger moveData
     * @param move2 smaller moveData
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
     * Generates a new particle set around a specific point with weights 0.5
     */
    @SuppressWarnings("SameParameterValue")
    @Contract(pure = true)
    @NotNull
    static Particle[] createNewParticleSet(SurfaceMap surfaceMap, @NotNull Pose centerPose, int numParticles) {
        Particle[] newParticles = new Particle[numParticles];

        float totalWeight = 0;

        for (int i = 0; i < numParticles; i++) {
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
            float newWeight = bellCurveFunction(averageError); //The closer the error to zero the closer the weight is to 1

            totalWeight += newWeight;

            newParticles[i] = new Particle(x, y, heading, newWeight);
        }

        newParticles = normalizeSet(newParticles, totalWeight);

        return newParticles;
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
    static Particle[] normalizeSet(@NotNull Particle[] particles, float totalWeight) {
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(particles[i], particles[i].weight / totalWeight);
        }

        return particles;
    }

    /**
     * Shifts a pose and applies noise
     * Modified version of the Odometry Pose Provider algorithm from the source code
     */
    @Contract(pure = true)
    @NotNull
    static Pose movePose(@NotNull Pose originalPose, @NotNull Move move, float angleNoiseFactor, float distanceNoiseFactor) {
        double dx = 0;
        double dy = 0;

        if (move.getMoveType() == Move.MoveType.TRAVEL) {
            double headingRad = (Math.toRadians(originalPose.getHeading()));
            dx = move.getDistanceTraveled() * Math.cos(headingRad);
            dy = move.getDistanceTraveled() * Math.sin(headingRad);
        } else if (move.getMoveType() == Move.MoveType.ARC) {
            double headingRad = (Math.toRadians(originalPose.getHeading()));
            double turnRad = Math.toRadians(move.getAngleTurned());
            double radius = move.getDistanceTraveled() / turnRad;
            dy = radius * (Math.cos(headingRad) - Math.cos(headingRad + turnRad));
            dx = radius * (Math.sin(headingRad + turnRad) - Math.sin(headingRad));
        }

        return new Pose(
                (float) (originalPose.getX() + dx + dx * random.nextGaussian() * distanceNoiseFactor),
                (float) (originalPose.getY() + dy + dy * random.nextGaussian() * distanceNoiseFactor),
                (float) normalizeHeading(originalPose.getHeading() + move.getAngleTurned() + move.getAngleTurned() * angleNoiseFactor * random.nextGaussian())
        );
    }

    @Contract(pure = true)
    static double normalizeHeading(double heading) {
        return heading % 360;
    }
}