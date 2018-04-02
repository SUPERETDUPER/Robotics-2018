/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.particles;

import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable particle (position + weight)
 */
public final class Particle {

    @NotNull
    private final Pose pose;
    public final float weight;

    public Particle(float x, float y, float heading, float weight) {
        this.pose = new Pose(x, y, heading);
        this.weight = weight;
    }

    public Particle(@NotNull Pose pose, float weight) {
        this(pose.getX(), pose.getY(), pose.getHeading(), weight); // Not directly this.pose = pose because want to keep object immutable
    }


    @NotNull
    public Pose getPose() {
        return new Pose(pose.getX(), pose.getY(), pose.getHeading()); //To make it immutable
    }

    @NotNull
    public Particle getParticleWithNewWeight(float weight) {
        return new Particle(pose, weight);
    }
}