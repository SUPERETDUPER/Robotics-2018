/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import common.particles.MCLData;
import common.particles.Particle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lejos.robotics.Transmittable;
import org.jetbrains.annotations.NotNull;

/**
 * Object that gets sent from the ev3 to the computer common.gui containing the particles particles and the currentPosition
 */
class ParticleDataLayer extends UpdatableLayer {
    private static final String LOG_TAG = ParticleDataLayer.class.getSimpleName();

    private final MCLData data = new MCLData(null, null);

    public ParticleDataLayer(int width, int height) {
        super(width, height);
    }

    @Override
    synchronized void displayOnGui(@NotNull GraphicsContext g) {
        if (data.getParticles() != null) {
            Particle[] normalizedParticles = Util.normalizeWeightTo255(data.getParticles());

            for (Particle particle : normalizedParticles) {
                g.setFill(Color.rgb((int) particle.weight, (int) (255 - particle.weight), 0));
                Util.displayPoseOnGui(g, particle.getPose());
            }
        }

        if (data.getCurrentPose() != null) {
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
        return data;
    }
}