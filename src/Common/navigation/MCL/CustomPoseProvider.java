package Common.navigation.MCL;

import Common.Config;
import Common.mapping.SurfaceMap;
import Common.utils.Logger;
import EV3.DataSender;
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

    private static final float DISTANCE_NOISE_FACTOR = 2;
    private static final float ANGLE_NOISE_FACTOR = 4;

    private static final float STARTING_RADIUS_NOISE = 10;
    private static final float STARTING_HEADING_NOISE = 10;

    private static final Random rand = new Random();

    private boolean inMove = false;

    private MCLData data = new MCLData();
    private MoveProvider moveProvider;

    public CustomPoseProvider(@NotNull MoveProvider mp) {
        this.moveProvider = mp;
        this.moveProvider.addMoveListener(this);

        generateNewParticleSet(new SurfaceReading());
    }

    private void generateNewParticleSet(Pose centerPose) {
        data.particles = new ArrayList<>(MCLData.NUM_PARTICLES);

        for (int i = 0; i < MCLData.NUM_PARTICLES; i++) {
            float rad = STARTING_RADIUS_NOISE * (float) rand.nextGaussian();

            float theta = (float) (2 * Math.PI * Math.random());  //Random angle between 0 and 2pi

            float x = centerPose.getX() + rad * (float) Math.cos(theta);
            float y = centerPose.getY() + rad * (float) Math.sin(theta);

            float heading = centerPose.getHeading() + STARTING_HEADING_NOISE * (float) rand.nextGaussian();
            data.particles.add(new Particle((new Pose(x, y, heading)), (float) Math.abs(rand.nextGaussian() + 0.5)));
        }
        updatePC();
    }

    private void generateNewParticleSet(Reading readings) {
        data.particles = new ArrayList<>(MCLData.NUM_PARTICLES);
        for (int i = 0; i < MCLData.NUM_PARTICLES; i++) {
            data.particles.add(generateParticle(readings));
        }
        updatePC();
    }

    @NotNull
    private Particle generateParticle(Reading readings) {
        Point point = SurfaceMap.get().getRandomPoint();

        // Pick a random angle
        float angle = (float) (Math.random() * 360);

        return new Particle(new Pose(point.x, point.y, angle), readings);
    }

    /**
     * Returns the best best estimate of the current currentPose;
     *
     * @return the estimated currentPose
     */
    @NotNull
    public Pose getPose() {
        if (inMove) {
            update(new SurfaceReading());
        }

        Logger.info(LOG_TAG, "Current pose is " + data.currentPose.toString());

        return data.currentPose;
    }

    public void setPose(@NotNull Pose pose) {
        data.currentPose = pose;
        generateNewParticleSet(pose);
    }

    public void update(Reading readings) {
        if (inMove) {
            // TODO : Will not work because move is not reset
            applyMove(moveProvider.getMovement());
        }

        calculateWeights(readings);
        resample();
        estimateCurrentPose();

        updatePC();
    }

    private void applyMove(@NotNull Move move) {
        ArrayList<Particle> oldParticles = data.particles;
        data.particles = new ArrayList<>(MCLData.NUM_PARTICLES);

        for (int i = 0; i < MCLData.NUM_PARTICLES; i++) {
            Particle particle = oldParticles.get(i);
            Pose pose = particle.getPose();

            float hypotenuse = move.getDistanceTraveled();
            double theta = Math.toRadians(pose.getHeading());

            float ym = hypotenuse * ((float) Math.sin(theta));
            float xm = hypotenuse * ((float) Math.cos(theta));

            float x = (float) (pose.getX() + xm + (DISTANCE_NOISE_FACTOR * xm * rand.nextGaussian()));
            float y = (float) (pose.getY() + ym + (DISTANCE_NOISE_FACTOR * ym * rand.nextGaussian()));

            float heading = ((pose.getHeading() + move.getAngleTurned() + (float) (move.getAngleTurned() * ANGLE_NOISE_FACTOR * rand.nextGaussian())) + 0.5F) % 360;

            data.particles.add(new Particle(x, y, heading, particle.getWeight()));
        }

        updatePC();
    }


    private void calculateWeights(@NotNull Reading readings) {
        ArrayList<Particle> oldParticles = data.particles;
        data.particles = new ArrayList<>(MCLData.NUM_PARTICLES);
        for (int i = 0; i < MCLData.NUM_PARTICLES; i++) {
            data.particles.add(new Particle(oldParticles.get(i).getPose(), readings));
        }
    }

    private void resample() {
        ArrayList<Particle> oldParticles = data.particles;
        data.particles = new ArrayList<>(MCLData.NUM_PARTICLES);

        int particlesGenerated = 0;
        int iterations = 0;

        while (iterations < MAX_RESAMPLE_ITERATIONS) {

            float randomFloat = (float) Math.random();

            //Copy particles with weight higher than random
            for (Particle particle : oldParticles) {
                if (particle.getWeight() >= randomFloat) {
                    data.particles.add(particle);
                    particlesGenerated++;

                    if (particlesGenerated >= MCLData.NUM_PARTICLES) {
                        return;
                    }
                }
            }
            iterations++;
        }

        if (particlesGenerated == 0) {
            generateNewParticleSet(new SurfaceReading());
            update(new SurfaceReading());
            Logger.warning(LOG_TAG, "Bad resample ; regenerated all particles");
        } else {
            for (int i = particlesGenerated; i < MCLData.NUM_PARTICLES; i++) {
                data.particles.add(data.particles.get(i % particlesGenerated));
            }

            Logger.warning(LOG_TAG, "Bad resample; had to duplicate existing particles");
        }
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

        for (Particle particle : data.particles) {
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

        data.currentPose = new Pose(estimatedX, estimatedY, estimatedAngle);
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        inMove = true;
    }

    public void moveStopped(@NotNull Move event, @NotNull MoveProvider mp) {
        update(new SurfaceReading());
        inMove = false;
    }

    private void updatePC() {
        if (Config.usePC) {
            DataSender.sendMCLData(this.data);
        }
    }
}
