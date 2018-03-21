/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common.GUI;

import Common.Config;
import Common.Particles.Particle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Object that gets sent from the EV3 to the computer Common.GUI containing the Particles particles and the currentPosition
 */
public class ParticleData implements Transmittable, Displayable {
    private static final String LOG_TAG = ParticleData.class.getSimpleName();

    private static final float DISPLAY_TAIL_LENGTH = 30;
    private static final float DISPLAY_TAIL_ANGLE = 10;

    private List<Particle> particles;
    private Pose currentPose;

    public ParticleData(List<Particle> particles, Pose currentPose) {
        this.particles = particles;
        this.currentPose = currentPose;
    }

    public Pose getCurrentPose() {
        return currentPose;
    }

    @Override
    public synchronized void displayOnGui(@NotNull GraphicsContext g) {
        if (particles != null) {
            g.setFill(Color.BLUE);

            for (Particle particle : particles) {
                displayPoseOnGui(particle.getPose(), g);
                if (Config.DISPLAY_PARTICLE_WEIGHT) {
                    displayParticleWeight(particle, g);
                }
            }
        }

        if (currentPose != null) {
            g.setFill(Color.RED);
            displayPoseOnGui(currentPose, g);
        }
    }

    private static void displayPoseOnGui(@NotNull Pose particlePose, @NotNull GraphicsContext g) {
        Point leftEnd = particlePose.pointAt(DISPLAY_TAIL_LENGTH, particlePose.getHeading() + 180 - DISPLAY_TAIL_ANGLE);
        Point rightEnd = particlePose.pointAt(DISPLAY_TAIL_LENGTH, particlePose.getHeading() + 180 + DISPLAY_TAIL_ANGLE);

        double[] xValues = new double[]{
                Math.round(particlePose.getX()),
                Math.round(leftEnd.x),
                Math.round(rightEnd.x)
        };

        double[] yValues = new double[]{
                Math.round(particlePose.getY()),
                Math.round(leftEnd.y),
                Math.round(rightEnd.y)
        };

        g.fillPolygon(xValues, yValues, xValues.length);
    }

    private static void displayParticleWeight(@NotNull Particle particle, GraphicsContext g) {
        g.fillText(String.valueOf(particle.getWeight()), Math.round(particle.getPose().getX()), Math.round(particle.getPose().getY()));
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
            dos.writeInt(particles.size());
            for (Particle particle : particles) {
                particle.getPose().dumpObject(dos);
                dos.writeFloat(particle.getWeight());
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
            particles = new ArrayList<>(numOfParticles);

            for (int i = 0; i < numOfParticles; i++) {
                Pose particlePose = new Pose();
                particlePose.loadObject(dis);

                particles.add(new Particle(particlePose, dis.readFloat()));
            }
        }
    }
}