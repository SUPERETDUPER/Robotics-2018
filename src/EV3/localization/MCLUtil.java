/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.localization;

import Common.Logger;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

final class MCLUtil {
    private static final String LOG_TAG = MCLUtil.class.getSimpleName();

    private static final Random random = new Random();

    @NotNull
    private static Pose rotatePose(@NotNull Pose pose, float angleToRotate, float randomFactor) {
        if (angleToRotate == 0) return pose;

        float heading = (pose.getHeading() + angleToRotate + (float) (angleToRotate * randomFactor * random.nextGaussian()) + 0.5F) % 360;

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
    static Pose movePose(@NotNull Pose pose, @NotNull Move move, float angleNoiseFactor, float distanceNoiseFactor) {
        switch (move.getMoveType()) {
            case STOP:
                return pose;
            case TRAVEL:
                return shiftPose(pose, move.getDistanceTraveled(), distanceNoiseFactor);
            case ROTATE:
                return rotatePose(pose, move.getAngleTurned(), angleNoiseFactor);
            default:
                Logger.warning(LOG_TAG, "Move type not implemented " + move.toString());
                return pose;
        }
    }

    @NotNull
    static Pose movePose(@NotNull Pose pose, @NotNull Move move) {
        return movePose(pose, move, 0, 0);
    }

    @NotNull
    static Move subtractMove(@NotNull Move move1, @Nullable Move move2) {
        if (move2 == null) {
            return move1;
        }

        return new Move(move1.getMoveType(), move1.getDistanceTraveled() - move2.getDistanceTraveled(), move1.getAngleTurned() - move2.getAngleTurned(), move1.isMoving());
    }
}