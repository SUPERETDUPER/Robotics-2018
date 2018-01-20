package navigation;

import geometry.SurfaceMap;
import lejos.robotics.Transmittable;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import utils.Logger;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Inspired by Lawrie Griffiths' MCLParticleSet class in LEJOS EV3 Source code
 */
public class CustomMCLParticleSet implements Transmittable {

    private static final String LOG_TAG = CustomMCLParticleSet.class.getSimpleName();

    // Static variables
    private static final int maxIterationsForResample = 1000;
    private static final float distanceNoiseFactor = 0.2f;
    private static final float angleNoiseFactor = 4f;


    // Instance variables
    private int numParticles = 200;
    private CustomMCLParticle[] particles = new CustomMCLParticle[numParticles];
    //private float maxWeight;

    //private static final boolean debug = false;

    CustomMCLParticleSet() {
        for (int i = 0; i < numParticles; i++) {
            particles[i] = generateParticle();
        }
    }

    public int getSize() {
        return numParticles;
    }

    /**
     * Generates a circular cloud of particles centered on initialPose with random
     * normal radius  and angle, and random normal heading.
     *
     * @param initialPose  the center of the cloud
     * @param radiusNoise  standard deviation of the normal of the distance from center
     * @param headingNoise standard deviation of heading
     */
    public void setInitialPose(Pose initialPose, float radiusNoise, float headingNoise) {
        particles = new CustomMCLParticle[numParticles];

        Random random = new Random();

        for (int i = 0; i < numParticles; i++) {
            float rad = radiusNoise * (float) random.nextGaussian();
            float theta = (float) (2 * Math.PI * Math.random());
            float x = initialPose.getX() + rad * (float) Math.cos(theta);
            float y = initialPose.getY() + rad * (float) Math.sin(theta);
            float heading = initialPose.getHeading() + headingNoise * (float) random.nextGaussian();
            particles[i] = new CustomMCLParticle((new Pose(x, y, heading)));
            //if (debug) {
            //  System.out.println(" new particle set ");
            //}
        }
    }

    /**
     * Generate a random particle within the mapped area.
     *
     * @return the particle
     */
    private CustomMCLParticle generateParticle() {
        float x;
        float y;

        do {
            x = (float) Math.random() * SurfaceMap.getBoundingRectangle().getWidth() + SurfaceMap.getBoundingRectangle().getX1();
            y = (float) Math.random() * SurfaceMap.getBoundingRectangle().getHeight() + SurfaceMap.getBoundingRectangle().getY1();

        } while (!SurfaceMap.contains(new Point(x, y)));

        // Pick a random angle
        float angle = ((float) Math.random()) * 360;

        return new CustomMCLParticle(new Pose(x, y, angle));
    }

    /**
     * Get a specific particle
     *
     * @param i the index of the particle
     * @return the particle
     */
    public CustomMCLParticle getParticle(int i) {
        return particles[i];
    }


    /**
     * Resample the set picking those with higher weights.
     * <p>
     * Note that the new set has multiple instances of the particles with higher
     * weights.
     *
     */
    public void resample() {
        CustomMCLParticle[] oldParticles = particles;
        particles = new CustomMCLParticle[numParticles];

        int particlesGenerated = 0;

        for (int iterations = 0; iterations < maxIterationsForResample; iterations++) {
            float rand = (float) Math.random();

            for (int i = 0; i < numParticles; i++) {

                if (particlesGenerated >= numParticles) {
                    return;
                }

                if (oldParticles[i].getWeight() >= rand) {
                    //TODO : Make sure this works might be mutable/immutable idk, see source

                    // Create a new instance of the particle and set its weight
                    particles[particlesGenerated] = new CustomMCLParticle(oldParticles[i].getPose());
                    particles[particlesGenerated].setWeight(oldParticles[i].getWeight());
                    particlesGenerated++;
                }
            }
        }


        if (particlesGenerated > 0) { // Duplicate the ones we have so far
            for (int i = particlesGenerated; i < numParticles; i++) {
                particles[i] = new CustomMCLParticle(particles[i % particlesGenerated].getPose());
                particles[i].setWeight(particles[i % particlesGenerated].getWeight());
            }
            Logger.warning(LOG_TAG, "Bad resample; had to duplicate existing particles");
        } else {
            for (int i = 0; i < numParticles; i++) {
                particles[i] = generateParticle();
            }
            Logger.warning(LOG_TAG, "Bad resample; regenerated all particles");
        }

    }


    /**
     * Calculate the weight for each particle
     */
    public void calculateWeights(int surfaceColor) {

        //maxWeight = 0f;
        for (int i = 0; i < numParticles; i++) {
            particles[i].calculateWeight(surfaceColor);
            //float weight = particles[i].getWeight();
            //if (weight > maxWeight) maxWeight = weight;
        }

        //if (debug) System.out.println("Calc Weights Max wt " + maxWeight + " Zeros " + zeros);
        //return maxWeight >= .01;
    }

    /**
     * Apply a move to each particle
     *
     * @param move the move to apply
     */
    public void applyMove(Move move) {
        if (move == null) {
            System.out.println("applyMove: null move");
            return;
        }
        //if (debug) System.out.println("particles applyMove " + move.getMoveType());
        //maxWeight = 0f;
        for (int i = 0; i < numParticles; i++) {
            particles[i].applyMove(move, distanceNoiseFactor, angleNoiseFactor);
        }
        //if (debug) System.out.println("particles applyMove Exit");
    }

    public void paintComponent(Graphics g) {
        for (CustomMCLParticle particle : particles) {
            if (particle != null) {
                particle.paintComponent(g);
            }
        }
    }

    public void dumpObject(DataOutputStream dos) throws IOException {
        dos.writeInt(numParticles);

        for (CustomMCLParticle particle : particles) {
            Pose pose = particle.getPose();
            float weight = particle.getWeight();
            dos.writeFloat(pose.getX());
            dos.writeFloat(pose.getY());
            dos.writeFloat(pose.getHeading());
            dos.writeFloat(weight);
        }
    }

    public void loadObject(DataInputStream dis) throws IOException {
        numParticles = dis.readInt();

        CustomMCLParticle[] newParticles = new CustomMCLParticle[numParticles];

        for (int i = 0; i < numParticles; ++i) {
            float x = dis.readFloat();
            float y = dis.readFloat();
            float angle = dis.readFloat();
            Pose pose = new Pose(x, y, angle);
            newParticles[i] = new CustomMCLParticle(pose);
            newParticles[i].setWeight(dis.readFloat());
        }

        this.particles = newParticles;
    }
}