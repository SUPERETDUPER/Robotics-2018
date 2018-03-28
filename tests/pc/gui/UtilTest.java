/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import common.particles.Particle;
import lejos.robotics.navigation.Pose;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilTest {

    @Test
    void normalizeWeightTo255() {
        Pose acceptedPose = new Pose(1, 1, 0);

        Particle[] unNormalizedParticles = {
                new Particle(acceptedPose, 0.4F),
                new Particle(acceptedPose, 0.3F),
                new Particle(acceptedPose, 0.2F),
                new Particle(acceptedPose, 0.1F)
        };

        Particle[] normalizedParticles = Util.normalizeWeightTo255(unNormalizedParticles);

        Assertions.assertEquals(normalizedParticles[0].weight, 255);
        Assertions.assertEquals(normalizedParticles[1].weight, 191.25);
        Assertions.assertEquals(normalizedParticles[2].weight, 127.5);
        Assertions.assertEquals(normalizedParticles[3].weight, 63.75);
    }
}