package Common.navigation.MCL;

import Common.mapping.SurfaceMap;
import Common.utils.Logger;
import com.sun.istack.internal.NotNull;
import lejos.robotics.geometry.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;

import java.util.ArrayList;
import java.util.Random;

/**
 * Inspired by Lawrie Griffiths' and Roger Glassey's MCLPoseProvider class in DataSender Lejos Source Code
 */

public class CustomPoseProvider implements PoseProvider, MoveListener {

    private static final String LOG_TAG = CustomPoseProvider.class.getSimpleName();

    private static final int MAX_RESAMPLE_ITERATIONS = 1000;

    private static final float DISTANCE_NOISE_FACTOR = 0.01F;
    private static final float ANGLE_NOISE_FACTOR = 0.2F;

    private static final float STARTING_RADIUS_NOISE = 10;
    private static final float STARTING_HEADING_NOISE = 10;

    private static final Random random = new Random();

    private boolean inMove = false;

    private final MCLData data = new MCLData();
    private final MoveProvider moveProvider;

    public CustomPoseProvider(@NotNull MoveProvider mp) {
        this.moveProvider = mp;
        this.moveProvider.addMoveListener(this);

        Readings readings = new SurfaceReadings();
        Logger.info(LOG_TAG, "Creating New Pose Provider with readings " + readings.toString());
        data.setParticles(generateNewParticleSet(readings));
    }

    /*
    Generates a new particle set around a specific point
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

    /*
    Generates a particle per the reading
     */
    private static ArrayList<Particle> generateNewParticleSet(Readings readings) {
        ArrayList<Particle> particles = new ArrayList<>(MCLData.NUM_PARTICLES);

        for (int i = 0; i < MCLData.NUM_PARTICLES; i++) {
            particles.add(generateParticle(readings));
        }

        return particles;
    }

    /*
    Generates a particle with a weight corresponding to the reading
     */
    @NotNull
    private static Particle generateParticle(Readings readings) {
        Point point = SurfaceMap.get().getRandomPoint();

        float angle = (float) (Math.random() * 360);

        return new Particle(point.x, point.y, angle, readings);
    }

    /**
     * Returns the best best estimate of the currentPose;
     *
     * @return the estimated currentPose
     */
    @NotNull
    public Pose getPose() {
        if (inMove) {
            update(new SurfaceReadings());
        }

        return data.getCurrentPose();
    }


    public void setPose(@NotNull Pose pose) {
        Logger.info(LOG_TAG, "Setting current pose to " + pose + "... particles regenerated");
        data.setParticlesAndCurrent(pose, generateNewParticleSet(pose));
    }

    private void update(Readings readings) {
        if (inMove) {
            applyMove(moveProvider.getMovement());
        }

        calculateWeights(readings);
        resample();
        estimateCurrentPose();
    }

    private void applyMove(@NotNull Move move) {
        ArrayList<Particle> newParticles = new ArrayList<>(MCLData.NUM_PARTICLES);

        switch (move.getMoveType()) {
            case STOP:
                return;
            case TRAVEL:
                for (Particle oldParticle : data.getParticles()) {
                    Pose pose = oldParticle.getPose();

                    float hypotenuse = move.getDistanceTraveled();
                    double theta = Math.toRadians(pose.getHeading());

                    float ym = hypotenuse * ((float) Math.sin(theta));
                    float xm = hypotenuse * ((float) Math.cos(theta));

                    float x = (float) (pose.getX() + xm + (DISTANCE_NOISE_FACTOR * xm * random.nextGaussian()));
                    float y = (float) (pose.getY() + ym + (DISTANCE_NOISE_FACTOR * ym * random.nextGaussian()));

                    float heading = ((pose.getHeading() + move.getAngleTurned() + (float) (move.getAngleTurned() * ANGLE_NOISE_FACTOR * random.nextGaussian())) + 0.5F) % 360;

                    newParticles.add(new Particle(x, y, heading, oldParticle.getWeight()));
                }
                Logger.info(LOG_TAG, "Particles moved by" + move.toString());
                break;
            default:
                Logger.error(LOG_TAG, "This move type has not been implemented yet");
                return;
        }

        data.setParticles(newParticles);
    }


    private void calculateWeights(@NotNull Readings readings) {
        ArrayList<Particle> newParticles = new ArrayList<>(MCLData.NUM_PARTICLES);

        for (Particle oldParticle : data.getParticles()) {
            newParticles.add(new Particle(oldParticle.getPose(), readings));
        }

        Logger.info(LOG_TAG, "Recalculated weights using readings" + readings.toString());
        data.setParticles(newParticles);
    }

    private void resample() {
        ArrayList<Particle> newParticles = new ArrayList<>(MCLData.NUM_PARTICLES);

        int particlesGenerated = 0;

        for (int i = 0; i < MAX_RESAMPLE_ITERATIONS; i++) {

            //Copy particles with weight higher than random
            for (Particle particle : data.getParticles()) {
                if (particle.getWeight() >= random.nextFloat()) {
                    newParticles.add(particle);
                    particlesGenerated++;

                    if (particlesGenerated == MCLData.NUM_PARTICLES) {
                        Logger.info(LOG_TAG, "Successful particle resample");
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
            Logger.warning(LOG_TAG, "Bad resample ; regenerated all particles");
        } else if (particlesGenerated < MCLData.NUM_PARTICLES) {
            for (int i = particlesGenerated; i < MCLData.NUM_PARTICLES; i++) {
                newParticles.add(newParticles.get(i % particlesGenerated));
            }

            Logger.warning(LOG_TAG, "Bad resample; had to duplicate existing particles");
        }

        data.setParticles(newParticles);
    }

    /**
     * Estimate currentPose from weighted average of the particles
     * Calculate statistics
     */
    private void estimateCurrentPose() {
        float totalWeights = 0;

        float estimatedX = 0;
        float estimatedY = 0;
        float estimatedAngle = 0;

        for (Particle particle : data.getParticles()) {
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

        Logger.info(LOG_TAG, "Estimating current pose to be " + data.getCurrentPose().toString());
        data.setCurrentPose(new Pose(estimatedX, estimatedY, estimatedAngle));
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        inMove = true;
    }

    public void moveStopped(@NotNull Move event, @NotNull MoveProvider mp) {
        update(new SurfaceReadings());
        inMove = false;
    }
}
