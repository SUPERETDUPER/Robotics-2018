/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import common.particles.MCLData;
import common.particles.Particle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

/**
 * Object that gets sent from the ev3 to the computer common.gui containing the particles particles and the currentPosition
 */
class ParticleDataLayer extends UpdatableLayer {
    // --Commented out by Inspection (25/04/18 8:38 PM):private static final String LOG_TAG = ParticleDataLayer.class.getSimpleName();

    private MCLData data;

    ParticleDataLayer(double width, double height) {
        super(width, height);
    }

    @Override
    synchronized void displayOnGui(@NotNull GraphicsContext g) {
        if (data != null) {
            Particle[] normalizedParticles = Util.normalizeWeightTo255(data.getParticles());

            for (Particle particle : normalizedParticles) {
                g.setFill(Color.rgb((int) particle.weight, (int) (255 - particle.weight), 0));
                Util.displayPoseOnGui(g, particle.getPose());
            }


            g.setFill(Color.BLUE);
            Util.displayPoseOnGui(g, data.getCurrentPose());
        }
    }

    @Override
    boolean shouldInvert() {
        return true;
    }

    @Override
    Transmittable getContent() {
        if (data == null){
            data = new MCLData(new Particle[0], new Pose());
        }

        return data;
    }
}