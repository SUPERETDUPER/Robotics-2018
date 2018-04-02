/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.particles;

import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Object that gets sent from the ev3 to the computer common.gui containing the particles particles and the currentPosition
 */
public class MCLData implements Transmittable {
    private static final String LOG_TAG = MCLData.class.getSimpleName();

    @Nullable
    private Particle[] particles;
    @Nullable
    private Pose currentPose;

    public MCLData(@Nullable Particle[] particles, @Nullable Pose currentPose) {
        this.particles = particles;
        this.currentPose = currentPose;
    }

    @Nullable
    public Pose getCurrentPose() {
        return currentPose;
    }

    @Nullable
    public Particle[] getParticles() {
        return particles;
    }

    public void setCurrentPose(@Nullable Pose currentPose) {
        this.currentPose = currentPose;
    }

    public void setParticles(@Nullable Particle[] particles) {
        this.particles = particles;
    }

    public synchronized void dumpObject(@NotNull DataOutputStream dos) throws IOException {
        boolean hasCurrentPose = currentPose != null;

        dos.writeBoolean(hasCurrentPose);
        if (hasCurrentPose) {
            currentPose.dumpObject(dos);
        }

        if (particles == null) {
            dos.writeInt(0);
        } else {
            dos.writeInt(particles.length);
            for (Particle particle : particles) {
                particle.getPose().dumpObject(dos);
                dos.writeFloat(particle.weight);
            }
        }
    }

    public synchronized void loadObject(@NotNull DataInputStream dis) throws IOException {
        if (dis.readBoolean()) {
            this.currentPose = new Pose();
            this.currentPose.loadObject(dis);
        }

        int numOfParticles = dis.readInt();

        if (numOfParticles != 0) {
            particles = new Particle[numOfParticles];

            for (int i = 0; i < numOfParticles; i++) {
                Pose particlePose = new Pose();
                particlePose.loadObject(dis);

                particles[i] = new Particle(particlePose, dis.readFloat());
            }
        }
    }
}