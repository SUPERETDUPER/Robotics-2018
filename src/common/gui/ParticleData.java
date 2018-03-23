/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.gui;

import common.Config;
import common.particles.Particle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;
import pc.displayable.Displayable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Object that gets sent from the ev3 to the computer common.gui containing the particles particles and the currentPosition
 */
public class ParticleData implements Transmittable {
    private static final String LOG_TAG = ParticleData.class.getSimpleName();

    protected Particle[] particles;
    protected Pose currentPose;

    public ParticleData(Particle[] particles, Pose currentPose) {
        this.particles = particles;
        this.currentPose = currentPose;
    }

    public Pose getCurrentPose() {
        return currentPose;
    }


    public void dumpObject(@NotNull DataOutputStream dos) throws IOException {
        dos.writeBoolean(currentPose != null);
        if (currentPose != null) {
            currentPose.dumpObject(dos);
        }

        if (particles == null) {
            dos.writeInt(0);
        }
        if (particles != null) {
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