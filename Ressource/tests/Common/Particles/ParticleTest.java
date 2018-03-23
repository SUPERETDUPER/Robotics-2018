/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.Particles;

import lejos.robotics.navigation.Pose;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParticleTest {
    @Test
    void immutableTest() {
        Pose pose = new Pose(0, 0, 0);
        Particle particle = new Particle(pose, 0);

        pose.translate(10, 10);
        pose.rotateUpdate(10);

        Pose newPose = particle.getPose();

        Assertions.assertNotEquals(newPose.getX(), pose.getX());
        Assertions.assertNotEquals(newPose.getY(), pose.getY());
        Assertions.assertNotEquals(newPose.getHeading(), pose.getHeading());

        //Part two
        particle = new Particle(0, 0, 0, 0);

        Pose particlePose = particle.getPose();
        particlePose.translate(10, 10);
        particlePose.rotateUpdate(10);

        Assertions.assertNotEquals(particlePose.getX(), particle.getPose().getX());
        Assertions.assertNotEquals(particlePose.getY(), particle.getPose().getY());
        Assertions.assertNotEquals(particlePose.getHeading(), particle.getPose().getHeading());
    }
}