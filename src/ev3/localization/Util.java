/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.Logger;
import common.particles.Particle;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

final class Util {
    private static final String LOG_TAG = Util.class.getSimpleName();

    private static final Random random = new Random();

    @NotNull
    private static Pose rotatePose(@NotNull Pose pose, float angleToRotate, float randomFactor) {
        if (angleToRotate == 0) return pose;

        float heading = (pose.getHeading() + angleToRotate + (float) (angleToRotate * randomFactor * random.nextGaussian())) % 360;

        return new Pose(pose.getX(), pose.getY(), heading);
    }

    @NotNull
    private static Pose shiftPose(@NotNull Pose pose, float distance, float randomFactor) {
        if (distance == 0) return pose;

        double theta = Math.toRadians(pose.getHeading());

        double ym = distance * Math.sin(theta);
        double xm = distance * Math.cos(theta);

        float x = (float) (pose.getX() + xm + randomFactor * xm * random.nextGaussian());
        float y = (float) (pose.getY() + ym + randomFactor * ym * random.nextGaussian());

        return new Pose(x, y, pose.getHeading());
    }

    @NotNull
    private static Pose movePose(@NotNull Pose pose, @NotNull Move move, float angleNoiseFactor, float distanceNoiseFactor) {
        switch (move.getMoveType()) {
            case STOP:
                return pose;
            case TRAVEL:
                return shiftPose(pose, move.getDistanceTraveled(), distanceNoiseFactor);
            case ROTATE:
                return rotatePose(pose, move.getAngleTurned(), angleNoiseFactor);
            default:
                Logger.warning(LOG_TAG, "Move type not implemented " + move.toString());
                throw new RuntimeException(move.toString() + " ... " + pose.toString());
        }
    }

    @NotNull
    static Pose movePose(@NotNull Pose pose, @NotNull Move move) {
        return movePose(pose, move, 0, 0);
    }

    static Particle[] moveParticleSet(@NotNull Particle[] particles, @NotNull Move move, float angleNoiseFactor, float distanceNoiseFactor) {
        Particle[] newParticles = new Particle[particles.length];

        for (int i = 0; i < particles.length; i++) {
            Pose newPose = Util.movePose(particles[i].getPose(), move, angleNoiseFactor, distanceNoiseFactor);
            newParticles[i] = new Particle(newPose, particles[i].weight);
        }

        return newParticles;
    }

    @NotNull
    static Move subtractMove(@NotNull Move move1, @Nullable Move move2) {
        if (move2 == null) {
            return move1;
        }

        if (move1.getMoveType() == Move.MoveType.ARC) {
            throw new RuntimeException(move1.toString() + " " + move1.getDistanceTraveled() + " " + move1.getAngleTurned());
        }

        return new Move(move1.getMoveType(), move1.getDistanceTraveled() - move2.getDistanceTraveled(), move1.getAngleTurned() - move2.getAngleTurned(), move1.isMoving());
    }

    static Pose deepCopyPose(Pose pose) {
        return new Pose(pose.getX(), pose.getY(), pose.getHeading());
    }
}