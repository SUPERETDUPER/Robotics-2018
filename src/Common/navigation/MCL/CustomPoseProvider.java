package Common.navigation.MCL;

import Common.Config;
import Common.mapping.SurfaceMap;
import Common.utils.Logger;
import EV3.DataSender;
import com.sun.istack.internal.NotNull;
import lejos.robotics.geometry.Point;
import lejos.robotics.localization.OdometryPoseProvider;
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

public class CustomPoseProvider implements PoseProvider, MoveListener {

    private static final String LOG_TAG = CustomPoseProvider.class.getSimpleName();

    private static final int MAX_RESAMPLE_ITERATIONS = 1000;

    private static final float DISTANCE_NOISE_FACTOR = 0.008F;
    private static final float ANGLE_NOISE_FACTOR = 0.04F;

    private static final float STARTING_RADIUS_NOISE = 4;
    private static final float STARTING_HEADING_NOISE = 3;

    private static final Random random = new Random();

    private final MCLData data = new MCLData(); //Stores all the data that needs to be transmitted to the computer
    private final OdometryPoseProvider odometryPoseProvider; //Keeps track of the robots location when it's moving
    private final MoveProvider moveProvider;


    public CustomPoseProvider(@NotNull MoveProvider moveProvider, Pose startingPose) {
        this.moveProvider = moveProvider;

        this.odometryPoseProvider = new OdometryPoseProvider(moveProvider) { //Odometry pose provider that updates MCLData
            @Override
            public synchronized Pose getPose() {
                Pose pose = super.getPose();
                data.setOdometryPose(pose);
                return pose;
            }

            @Override
            public synchronized void setPose(Pose aPose) {
                super.setPose(aPose);
                data.setOdometryPose(aPose);
            }
        };

        moveProvider.addMoveListener(this);


        Logger.info(LOG_TAG, "Setting current pose to " + startingPose + "... particles regenerated");
        data.setCurrentPose(startingPose);
        data.setParticles(generateNewParticleSet(startingPose));
        odometryPoseProvider.setPose(startingPose);
        updatePC(data);
    }

    private static void updatePC(MCLData data) {
        if (Config.usePC) {
            DataSender.sendMCLData(data);
        }
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
        Point point = SurfaceMap.get().getRandomPoint();

        float angle = (float) (Math.random() * 360);

        return new Particle(point.x, point.y, angle, readings);
    }

    private static ArrayList<Particle> getMovedParticleSet(ArrayList<Particle> particles, @NotNull float distanceTraveled, float angleTurned) {
        ArrayList<Particle> newParticles = new ArrayList<>(MCLData.NUM_PARTICLES);

        for (Particle oldParticle : particles) {
            Pose pose = oldParticle.getPose();

            double theta = Math.toRadians(pose.getHeading());

            float ym = distanceTraveled * ((float) Math.sin(theta));
            float xm = distanceTraveled * ((float) Math.cos(theta));

            float x = (float) (pose.getX() + xm + (DISTANCE_NOISE_FACTOR * xm * random.nextGaussian()));
            float y = (float) (pose.getY() + ym + (DISTANCE_NOISE_FACTOR * ym * random.nextGaussian()));

            float heading = (pose.getHeading() + angleTurned + (float) (angleTurned * ANGLE_NOISE_FACTOR * random.nextGaussian()) + 0.5F) % 360;


            newParticles.add(new Particle(x, y, heading, oldParticle.getWeight()));
        }
        Logger.debug(LOG_TAG, "Particles moved by " + distanceTraveled + " and turned by " + angleTurned);


        return newParticles;
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

        Logger.debug(LOG_TAG, "Estimating current pose to be " + newPose.toString());
        return newPose;
    }

    /**
     * Returns the currentPose;
     */
    @NotNull
    public Pose getPose() {
        if (moveProvider.getMovement().getMoveType() != Move.MoveType.STOP) {
            update(new SurfaceReadings());
        }

        return data.getCurrentPose();
    }

    public void setPose(@NotNull Pose pose) {
        Logger.warning(LOG_TAG, "Did not implement changing pose");
    }

    public PoseProvider getOdometryPoseProvider() {
        return odometryPoseProvider;
    }

    private void update(Readings readings) {
        float distanceTraveled = data.getCurrentPose().distanceTo(odometryPoseProvider.getPose().getLocation());
        float angleTurned = data.getCurrentPose().getHeading() - odometryPoseProvider.getPose().getHeading();


        data.setParticles(getMovedParticleSet(data.getParticles(), distanceTraveled, angleTurned)); //Moves particles over if odometry has moved since last time

        data.setParticles(getReWeightedParticleSet(data.getParticles(), readings)); //Recalculate all the particle weights

        data.setParticles(getResampledParticleSet(data.getParticles())); //Re samples for highest weights

        data.setCurrentPose(getCurrentPoseEstimate(data.getParticles())); //Updates current pose
        odometryPoseProvider.setPose(data.getCurrentPose()); //Updates odometryPoseProvider

        updatePC(data); //SendToPc
    }

    @Override
    public void moveStarted(Move event, MoveProvider moveProvider) {
        Logger.debug(LOG_TAG, "STARTED " + event.toString());
    }

    public void moveStopped(@NotNull Move event, @NotNull MoveProvider mp) {
        Logger.debug(LOG_TAG, "STOPPED " + event.toString());
        update(new SurfaceReadings());
    }
}