package EV3.navigation;

import Common.Config;
import Common.MCL.MCLData;
import Common.MCL.Particle;
import Common.mapping.SurfaceMap;
import Common.utils.Logger;
import EV3.DataSender;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import lejos.robotics.geometry.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;

import java.util.ArrayList;
import java.util.Random;

/**
 * Inspired by Lawrie Griffiths' and Roger Glassey's MCLPoseProvider class in Lejos Source Code
 */

//TODO Still not working
public class CustomMCLPoseProvider implements PoseProvider, MoveListener {

    private static final String LOG_TAG = CustomMCLPoseProvider.class.getSimpleName();

    private static final int MAX_RESAMPLE_ITERATIONS = 1000;

    private static final float DISTANCE_NOISE_FACTOR = 0.008F;
    private static final float ANGLE_NOISE_FACTOR = 0.04F;

    private static final float STARTING_RADIUS_NOISE = 4;
    private static final float STARTING_HEADING_NOISE = 3;

    private static final Random random = new Random();

    private final MoveProvider mp;

    private ArrayList<Particle> particles;
    private Pose currentPose;

    private float distanceTraveled;
    private float angleRotated;

    public CustomMCLPoseProvider(@NotNull MoveProvider moveProvider, Pose startingPose) {
        this.mp = moveProvider;
        moveProvider.addMoveListener(this);

        this.currentPose = startingPose;
        this.particles = generateNewParticleSet(startingPose);

        Logger.info(LOG_TAG, "Setting current pose to " + startingPose + ". particles generated");

        updatePC();
    }

    private void updatePC() {
        if (Config.usePC) {
            DataSender.sendMCLData(new MCLData(particles, currentPose));
        }
        //Brick.waitForUserConfirmation();
    }

    /**
     * Returns the currentPose;
     */
    @NotNull
    public Pose getPose() {
        update(null, null);

        return currentPose;
    }

    // TODO Doesn't work look at debug
    public synchronized void update(@Nullable Readings readings, @Nullable Move move) {
        if (move == null) {
            move = mp.getMovement();
        }

        boolean particlesShifted = moveParticles(move);

        if (particlesShifted) {
            Logger.info(LOG_TAG, "Particles shifted : " + move.toString());
        }

        if (readings != null) {
            weightParticles(readings); //Recalculate all the particle weights
            resample();//Re samples for highest weights
            Logger.info(LOG_TAG, "Recalculated weights + resample");
        }

        if (particlesShifted || readings != null) {
            estimatePose(); //Updates current pose
            Logger.info(LOG_TAG, "New position is " + currentPose.toString());

            updatePC(); //SendToPc
        }
    }

    @Override
    public synchronized void moveStarted(Move move, MoveProvider moveProvider) {
        distanceTraveled = 0;
        angleRotated = 0;
        Logger.info(LOG_TAG, "Move started " + move.toString());
    }

    @Override
    public synchronized void moveStopped(Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Move stopped " + move.toString());
        update(null, move);
    }

    private synchronized boolean moveParticles(Move move) {
        switch (move.getMoveType()) {
            case STOP:
                return false;
            case TRAVEL:
                if (move.getDistanceTraveled() - distanceTraveled == 0) {
                    return false;
                }

                shiftParticles(move.getDistanceTraveled() - distanceTraveled);
                distanceTraveled = move.getDistanceTraveled();
                return true;

            case ROTATE:
                if (move.getAngleTurned() - angleRotated == 0) {
                    return false;
                }

                rotateParticles(move.getAngleTurned() - angleRotated);
                angleRotated = move.getAngleTurned();

                return true;

            default:
                Logger.warning(LOG_TAG, "Move type not implemented " + move.toString());
                return false;
        }
    }

    private synchronized void rotateParticles(float angleRotated) {
        for (int i = 0; i < particles.size(); i++) {
            Pose particlePose = particles.get(i).getPose();

            float heading = (particlePose.getHeading() + angleRotated + (float) (angleRotated * ANGLE_NOISE_FACTOR * random.nextGaussian()) + 0.5F) % 360;

            particles.set(i, new Particle(particlePose.getX(), particlePose.getY(), heading, particles.get(i).getWeight()));
        }
    }

    private synchronized void shiftParticles(@NotNull float distance) {
        for (int i = 0; i < particles.size(); i++) {
            Pose pose = particles.get(i).getPose();

            double theta = Math.toRadians(pose.getHeading());

            double ym = distance * Math.sin(theta);
            double xm = distance * Math.cos(theta);

            float x = (float) (pose.getX() + xm + DISTANCE_NOISE_FACTOR * xm * random.nextGaussian());
            float y = (float) (pose.getY() + ym + DISTANCE_NOISE_FACTOR * ym * random.nextGaussian());


            particles.set(i, new Particle(x, y, pose.getHeading(), particles.get(i).getWeight()));
        }
    }

    private synchronized void weightParticles(@NotNull Readings readings) {
        for (int i = 0; i < particles.size(); i++) {
            Pose pose = particles.get(i).getPose();
            particles.set(i, new Particle(pose, readings.calculateWeight(pose)));
        }

        Logger.debug(LOG_TAG, "Recalculated weights using readings" + readings.toString());
    }

    private synchronized void resample() {
        ArrayList<Particle> newParticles = new ArrayList<>(MCLData.NUM_PARTICLES);

        int particlesGenerated = 0;

        for (int i = 0; i < MAX_RESAMPLE_ITERATIONS; i++) {

            //Copy particles with weight higher than random
            for (Particle particle : particles) {
                if (particle.getWeight() >= random.nextFloat()) {
                    newParticles.add(particle);
                    particlesGenerated++;

                    if (particlesGenerated == MCLData.NUM_PARTICLES) {
                        Logger.debug(LOG_TAG, "Successful particle resample");
                        break;
                    }
                }
            }

            if (particlesGenerated == MCLData.NUM_PARTICLES) {
                break;
            }
        }

        if (particlesGenerated == 0) {
            newParticles = generateNewParticleSet();
            Logger.warning(LOG_TAG, "Bad resample ; regenerated all particles");
        } else if (particlesGenerated < MCLData.NUM_PARTICLES) {
            for (int i = particlesGenerated; i < MCLData.NUM_PARTICLES; i++) {
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
    private synchronized void estimatePose() {
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

        currentPose = new Pose(estimatedX, estimatedY, estimatedAngle);
    }

    public void setPose(@NotNull Pose pose) {
        Logger.warning(LOG_TAG, "Did not implement changing pose");
    }

    /**
     * Generates a new particle set around a specific point with weights 0.5
     */
    @NotNull
    private static ArrayList<Particle> generateNewParticleSet(@NotNull Pose centerPose) {
        ArrayList<Particle> particles = new ArrayList<>(MCLData.NUM_PARTICLES);

        for (int i = 0; i < MCLData.NUM_PARTICLES; i++) {
            float rad = STARTING_RADIUS_NOISE * (float) random.nextGaussian();

            float theta = (float) (2 * Math.PI * Math.random());  //Random angle between 0 and 2pi

            float x = centerPose.getX() + rad * (float) Math.cos(theta);
            float y = centerPose.getY() + rad * (float) Math.sin(theta);

            float heading = centerPose.getHeading() + STARTING_HEADING_NOISE * (float) random.nextGaussian();
            particles.add(new Particle((new Pose(x, y, heading)), 0.5F));
        }

        return particles;
    }

    /**
     * Generates a new particle set per the reading
     */
    @NotNull
    private static ArrayList<Particle> generateNewParticleSet() {
        ArrayList<Particle> particles = new ArrayList<>(MCLData.NUM_PARTICLES);

        for (int i = 0; i < MCLData.NUM_PARTICLES; i++) {
            particles.add(generateParticle());
        }

        return particles;
    }

    /**
     * Generates a particle with a random position and weight corresponding to the reading
     */
    @NotNull
    private static Particle generateParticle() {
        Point point = SurfaceMap.getRandomPoint();

        float angle = (float) (Math.random() * 360);

        return new Particle(point.x, point.y, angle);
    }
}