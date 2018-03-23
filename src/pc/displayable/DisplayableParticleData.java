/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.displayable;

import common.Config;
import common.gui.ParticleData;
import common.particles.Particle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Object that gets sent from the ev3 to the computer common.gui containing the particles particles and the currentPosition
 */
public class DisplayableParticleData extends ParticleData implements Displayable {
    private static final String LOG_TAG = DisplayableParticleData.class.getSimpleName();

    private static final float DISPLAY_TAIL_LENGTH = 30;
    private static final float DISPLAY_TAIL_ANGLE = 10;

    public DisplayableParticleData(Particle[] particles, Pose currentPose) {
        super(particles, currentPose);
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
        g.fillText(String.valueOf(particle.weight), Math.round(particle.getPose().getX()), Math.round(particle.getPose().getY()));
    }

    @Override
    public boolean invert() {
        return true;
    }
}