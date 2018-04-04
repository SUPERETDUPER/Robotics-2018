/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.particles;

import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Object that gets sent from the ev3 to the computer common.gui containing the particles particles and the currentPosition
 */
public class MCLData implements Transmittable {
    private static final String LOG_TAG = MCLData.class.getSimpleName();

    @NotNull
    private Particle[] particles;
    @NotNull
    private Pose currentPose;

    public MCLData(@NotNull Particle[] particles, @NotNull Pose currentPose) {
        this.particles = particles;
        this.currentPose = currentPose;
    }

    @NotNull
    public Pose getCurrentPose() {
        return currentPose;
    }

    @NotNull
    public Particle[] getParticles() {
        return particles;
    }

    public void setCurrentPose(@NotNull Pose currentPose) {
        this.currentPose = currentPose;
    }

    public void setParticles(@NotNull Particle[] particles) {
        this.particles = particles;
    }

    public synchronized void dumpObject(@NotNull DataOutputStream dos) throws IOException {
        currentPose.dumpObject(dos);


        dos.writeInt(particles.length);
        for (Particle particle : particles) {
            particle.getPose().dumpObject(dos);
            dos.writeFloat(particle.weight);
        }
    }

    public synchronized void loadObject(@NotNull DataInputStream dis) throws IOException {
        this.currentPose = new Pose();
        this.currentPose.loadObject(dis);

        particles = new Particle[dis.readInt()];

        for (int i = 0; i < particles.length; i++) {
            Pose particlePose = new Pose();
            particlePose.loadObject(dis);

            particles[i] = new Particle(particlePose, dis.readFloat());
        }
    }
}