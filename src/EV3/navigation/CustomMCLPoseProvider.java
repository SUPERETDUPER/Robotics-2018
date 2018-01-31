package EV3.navigation;

import Common.Config;
import Common.mapping.SurfaceMap;
import Common.MCL.MCLData;
import Common.MCL.Particle;
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
import lejos.utility.Delay;

import java.util.ArrayList;
import java.util.Random;

/**
 * Inspired by Lawrie Griffiths' and Roger Glassey's MCLPoseProvider class in Lejos Source Code
 */

public class CustomMCLPoseProvider implements PoseProvider, MoveListener {

    private static final String LOG_TAG = CustomMCLPoseProvider.class.getSimpleName();

    private static final int MAX_RESAMPLE_ITERATIONS = 1000;

    private static final float DISTANCE_NOISE_FACTOR = 0.008F;
    private static final float ANGLE_NOISE_FACTOR = 0.04F;

    private static final float STARTING_RADIUS_NOISE = 4;
    private static final float STARTING_HEADING_NOISE = 3;

    private static final Random random = new Random();

    private final MCLData data = new MCLData(); //Stores all the data that needs to be transmitted to the computer

    private final MoveProvider mp;
    private float distanceTraveled;
    private float angleRotated;

    public CustomMCLPoseProvider(@NotNull MoveProvider moveProvider, Pose startingPose) {
        this.mp = moveProvider;

        Logger.info(LOG_TAG, "Setting current pose to " + startingPose + ". particles generated");
        data.setCurrentPose(startingPose);
        data.setParticles(generateNewParticleSet(startingPose));

        updatePC(data);

        moveProvider.addMoveListener(this);
    }

    private static void updatePC(MCLData data) {
        if (Config.usePC) {
            DataSender.sendMCLData(data);
        }
    }

    /**
     * Returns the currentPose;
     */
    @NotNull
    public synchronized Pose getPose() {
        update(null, null);

        return data.getCurrentPose();
    }

    // TODO Doesn`t work look at debug
    private synchronized void update(@Nullable Readings readings, @Nullable Move move) {
        ArrayList<Particle> newParticles = data.getParticles();

        if (move == null){
            move = mp.getMovement();
        }

        switch (move.getMoveType()) {
            case STOP:
                move = null;
                break;
            case TRAVEL:
                if (move.getDistanceTraveled() - distanceTraveled > 0) {
                    newParticles = getTraveledParticleSet(newParticles, move.getDistanceTraveled() - distanceTraveled);
                    distanceTraveled = move.getDistanceTraveled();
                } else {
                    move = null;
                }
                break;
            case ROTATE:
                if (move.getAngleTurned() - angleRotated > 0) {
                    newParticles = getRotatedParticleSet(newParticles, move.getAngleTurned() - angleRotated);
                    angleRotated = move.getAngleTurned();
                } else {
                    move = null;
                }
                break;
            case ARC:
                if (move.getAngleTurned() - angleRotated > 0 && move.getDistanceTraveled() - distanceTraveled > 0) {
                    newParticles = getArcParticleSet(newParticles,
                            move.getAngleTurned() - angleRotated,
                            move.getDistanceTraveled() - distanceTraveled);
                    angleRotated = move.getAngleTurned();
                    distanceTraveled = move.getDistanceTraveled();
                } else {
                    move = null;
                }
                break;
            default:
                Logger.warning(LOG_TAG, "Move type not implemented " + move.toString());
        }

        if (readings != null) {
            newParticles = getReWeightedParticleSet(newParticles, readings); //Recalculate all the particle weights
            newParticles = getResampledParticleSet(newParticles);//Re samples for highest weights
        }

        if (move != null || readings != null) {
            data.setParticles(newParticles);
            data.setCurrentPose(getCurrentPoseEstimate(data.getParticles())); //Updates current pose

            if (move != null) {
                Logger.info(LOG_TAG, "Moved particles " + move.toString());
            }
            if (readings != null){
                Logger.info(LOG_TAG, "Recalculated weights + resample");
            }
            Logger.info(LOG_TAG, "New position is " + data.getCurrentPose().toString());

            updatePC(data); //SendToPc
        }
    }

    //TODO When start called before stop doesn't work

    @Override
    public synchronized void moveStarted(Move move, MoveProvider moveProvider) {
        //Delay.msDelay(2000); //TODO Make it work without delay
        distanceTraveled = 0;
        angleRotated = 0;
        Logger.info(LOG_TAG, "Move started " + move.toString());
    }

    @Override
    public synchronized void moveStopped(Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Move stopped " + move.toString());
        update(null, move);

    }

    /**
     * Generates a new particle set around a specific point with weights 0.5
     */
    private static ArrayList<Particle> generateNewParticleSet(Pose centerPose) {
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
    private static ArrayList<Particle> generateNewParticleSet(Readings readings) {
        ArrayList<Particle> particles = new ArrayList<>(MCLData.NUM_PARTICLES);

        for (int i = 0; i < MCLData.NUM_PARTICLES; i++) {
            particles.add(generateParticle(readings));
        }

        return particles;
    }

    /**
     * Generates a particle with a random position and weight corresponding to the reading
     */
    @NotNull
    private static Particle generateParticle(Readings readings) {
        Point point = SurfaceMap.getRandomPoint();

        float angle = (float) (Math.random() * 360);

        return new Particle(point.x, point.y, angle, readings);
    }

    private static ArrayList<Particle> getArcParticleSet(ArrayList<Particle> particles, float angle, float distance) {
        //TODO generate randomness
        for (int i = 0 ; i < particles.size() ; i++) {
            Pose pose = particles.get(i).getPose();
            double dx = 0.0D;
            double dy = 0.0D;
            double headingRad = Math.toRadians((double) pose.getHeading());
            if (Math.abs(angle) >= 0.2F) {
                double turnRad = Math.toRadians((double) angle);
                double radius = (double) distance / turnRad;
                dy = radius * (Math.cos(headingRad) - Math.cos(headingRad + turnRad));
                dx = radius * (Math.sin(headingRad + turnRad) - Math.sin(headingRad));

            }

            float x = (float) ((double) pose.getX() + dx);
            float y = (float) ((double) pose.getY() + dy);
            float heading = (pose.getHeading() + angle + 0.5F) % 360;

            particles.set(i, new Particle(x, y, heading, particles.get(i).getWeight()));
        }

        Logger.warning(LOG_TAG, "Particles arced. Distance " + distance + ". Angle " + angle + ". Not tested");

        return particles;
    }

    private static ArrayList<Particle> getRotatedParticleSet(ArrayList<Particle> particles, float angleRotated) {
        for (int i = 0; i < particles.size(); i++) {
            Pose particlePose = particles.get(i).getPose();

            float heading = (particlePose.getHeading() + angleRotated + (float) (angleRotated * ANGLE_NOISE_FACTOR * random.nextGaussian()) + 0.5F) % 360;

            particles.set(i, new Particle(particlePose.getX(), particlePose.getY(), heading, particles.get(i).getWeight()));
        }

        return particles;
    }

    private static ArrayList<Particle> getTraveledParticleSet(ArrayList<Particle> particles, @NotNull float distanceTraveled) {
        for (int i = 0; i < particles.size(); i++) {
            Pose pose = particles.get(i).getPose();

            double theta = Math.toRadians(pose.getHeading());

            double ym = distanceTraveled * Math.sin(theta);
            double xm = distanceTraveled * Math.cos(theta);

            float x = (float) (pose.getX() + xm + DISTANCE_NOISE_FACTOR * xm * random.nextGaussian());
            float y = (float) (pose.getY() + ym + DISTANCE_NOISE_FACTOR * ym * random.nextGaussian());


            particles.set(i, new Particle(x, y, pose.getHeading(), particles.get(i).getWeight()));
        }

        return particles;
    }

    private static ArrayList<Particle> getReWeightedParticleSet(ArrayList<Particle> particles, @NotNull Readings readings) {
        ArrayList<Particle> newParticles = new ArrayList<>(MCLData.NUM_PARTICLES);

        for (Particle particle : particles) {
            newParticles.add(new Particle(particle.getPose(), readings));
        }

        Logger.debug(LOG_TAG, "Recalculated weights using readings" + readings.toString());
        return newParticles;
    }

    private static ArrayList<Particle> getResampledParticleSet(ArrayList<Particle> particles) {
        ArrayList<Particle> newParticles = new ArrayList<>(MCLData.NUM_PARTICLES);

        int particlesGenerated = 0;

        for (int i = 0; i < MAX_RESAMPLE_ITERATIONS; i++) {

            //Copy particles with weight higher than random
            for (Particle particle : particles) {
                if (particle.getWeight() >= random.nextFloat()) {
                    newParticles.add(particle);
                    particlesGenerated++;

                    if (particlesGenerated == MCLData.NUM_PARTICLES) {
                        Logger.debug(LOG_TAG, "Successful particle getResampledParticleSet");
                        break;
                    }
                }
            }

            if (particlesGenerated == MCLData.NUM_PARTICLES) {
                break;
            }
        }

        if (particlesGenerated == 0) {
            newParticles = generateNewParticleSet(new SurfaceReadings());
            Logger.warning(LOG_TAG, "Bad getResampledParticleSet ; regenerated all particles");
        } else if (particlesGenerated < MCLData.NUM_PARTICLES) {
            for (int i = particlesGenerated; i < MCLData.NUM_PARTICLES; i++) {
                newParticles.add(newParticles.get(i % particlesGenerated));
            }

            Logger.warning(LOG_TAG, "Bad getResampledParticleSet; had to duplicate existing particles");
        }

        return newParticles;
    }

    /**
     * Estimate currentPose from weighted average of the particles
     * Calculate statistics
     */
    private static Pose getCurrentPoseEstimate(ArrayList<Particle> particles) {
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

        Pose newPose = new Pose(estimatedX, estimatedY, estimatedAngle);

        return newPose;
    }

    public void setPose(@NotNull Pose pose) {
        Logger.warning(LOG_TAG, "Did not implement changing pose");
    }
}