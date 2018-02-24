/*
 * MIT License
 *
 * Copyright (c) [2018] [Martin Staadecker]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package EV3.navigation;

import Common.Logger;
import Common.Particles.Particle;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class ParticleSet {
    private static final String LOG_TAG = ParticleSet.class.getSimpleName();

    private static final float STARTING_RADIUS_NOISE = 4;
    private static final float STARTING_HEADING_NOISE = 3;

    private static final float DISTANCE_NOISE_FACTOR = 0.008F;
    private static final float ANGLE_NOISE_FACTOR = 0.04F;

    private static final Random random = new Random();

    private static final int NUM_PARTICLES = 200;
    private static final int MAX_RESAMPLE_ITERATIONS = 1000;

    private List<Particle> particles;

    ParticleSet(Pose startingPose) {
        this.particles = ParticleSet.getNewParticleSet(startingPose);
    }

    List<Particle> getParticles() {
        return particles;
    }

    synchronized void moveParticles(@NotNull Move move) {
        Logger.info(LOG_TAG, "Moving particles by : " + move.toString());

        List<Particle> newParticles = new ArrayList<>(NUM_PARTICLES);

        for (Particle particle : particles) {
            Pose newPose = MCLUtil.movePose(particle.getPose(), move, ANGLE_NOISE_FACTOR, DISTANCE_NOISE_FACTOR);
            newParticles.add(new Particle(newPose, particle.getWeight()));

        }

        particles = newParticles;
    }

    synchronized void weightParticles(@NotNull Readings readings) {
        List<Particle> newParticles = new ArrayList<>(NUM_PARTICLES);

        for (Particle particle : particles) {
            newParticles.add(new Particle(particle.getPose(), readings.calculateWeight(particle.getPose())));
        }

        particles = newParticles;

        Logger.debug(LOG_TAG, "Recalculated weights using readings" + readings.toString());
    }

    synchronized void resample() {
        ArrayList<Particle> newParticles = new ArrayList<>(ParticleSet.NUM_PARTICLES);

        int particlesGenerated = 0;

        for (int i = 0; i < MAX_RESAMPLE_ITERATIONS; i++) {

            //Copy particles with weight higher than random
            for (Particle particle : particles) {
                if (particle.getWeight() >= random.nextFloat()) {
                    newParticles.add(particle);
                    particlesGenerated++;

                    if (particlesGenerated == ParticleSet.NUM_PARTICLES) {
                        Logger.debug(LOG_TAG, "Successful particle resample");
                        break;
                    }
                }
            }

            if (particlesGenerated == ParticleSet.NUM_PARTICLES) {
                break;
            }
        }

        if (particlesGenerated == 0) {
            Logger.error(LOG_TAG, "Bad resample ; totally lost");
        } else if (particlesGenerated < ParticleSet.NUM_PARTICLES) {
            for (int i = particlesGenerated; i < ParticleSet.NUM_PARTICLES; i++) {
                newParticles.add(newParticles.get(i % particlesGenerated));
            }

            Logger.warning(LOG_TAG, "Bad resample; had to duplicate existing particles");
        }

        particles = newParticles;
    }

    /**
     * Estimate currentPose from weighted average of the particles
     * Calculate statistics
     */
    synchronized Pose estimateCurrentPose() {
        float totalWeights = 0;

        float estimatedX = 0;
        float estimatedY = 0;
        float estimatedAngle = 0;

        for (Particle particle : particles) {
            estimatedX += (particle.getPose().getX() * particle.getWeight());
            estimatedY += (particle.getPose().getY() * particle.getWeight());
            estimatedAngle += (particle.getPose().getHeading() * particle.getWeight());

            totalWeights += particle.getWeight();
        }

        estimatedX /= totalWeights;
        estimatedY /= totalWeights;
        estimatedAngle /= totalWeights;

        // Normalize angle
        while (estimatedAngle > 180) estimatedAngle -= 360;
        while (estimatedAngle < -180) estimatedAngle += 360;

        return new Pose(estimatedX, estimatedY, estimatedAngle);
    }

    /**
     * Generates a new particle set around a specific point with weights 0.5
     */
    @NotNull
    private static ArrayList<Particle> getNewParticleSet(@NotNull Pose centerPose) {
        ArrayList<Particle> newParticles = new ArrayList<>(NUM_PARTICLES);

        for (int i = 0; i < NUM_PARTICLES; i++) {
            float rad = STARTING_RADIUS_NOISE * (float) random.nextGaussian();

            float theta = (float) (2 * Math.PI * Math.random());  //Random angle between 0 and 2pi

            float x = centerPose.getX() + rad * (float) Math.cos(theta);
            float y = centerPose.getY() + rad * (float) Math.sin(theta);

            float heading = centerPose.getHeading() + STARTING_HEADING_NOISE * (float) random.nextGaussian();
            newParticles.add(new Particle((new Pose(x, y, heading)), 0.5F));
        }

        return newParticles;
    }
}
