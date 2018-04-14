/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.particles;

import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

/**
 * A particle has a position (x + y + heading) and a weight
 * This object is immutable (its content cannot change once it has been created)
 */
public final class Particle {

    @NotNull
    private final Pose pose; //Pose is mutable therefor must take extra steps to protect object's immutability
    public final float weight;

    public Particle(float x, float y, float heading, float weight) {
        this.pose = new Pose(x, y, heading);
        this.weight = weight;
    }

    public Particle(@NotNull Pose pose, float weight) {
        this(pose.getX(), pose.getY(), pose.getHeading(), weight); // Not directly this.pose = pose because want to keep object immutable
    }

    /**
     * Keeps the inputted particles position but uses another weight
     *
     * @param particle the particle to use it's pose
     * @param weight   the weight
     */
    public Particle(@NotNull Particle particle, float weight) {
        this.pose = particle.pose;
        this.weight = weight;
    }


    @NotNull
    public Pose getPose() {
        return new Pose(pose.getX(), pose.getY(), pose.getHeading()); //To make it immutable
    }
}