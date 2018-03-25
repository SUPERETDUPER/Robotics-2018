/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common;

import common.particles.Particle;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestUtils {
    @Test
    public static void assertPoseEqual(Pose pose1, Pose pose2) {
        Assertions.assertEquals(pose1.getX(), pose2.getX());
        Assertions.assertEquals(pose1.getY(), pose2.getY());
        Assertions.assertEquals(pose1.getHeading(), pose2.getHeading());
    }

    @Contract(pure = true)
    private static boolean sumOfWeightsIsOne(@NotNull Particle[] particles) {
        float totalWeight = 0;

        for (Particle particle : particles) {
            totalWeight += particle.weight;
        }

        return totalWeight > 0.99 && totalWeight < 1.01;
    }
}
