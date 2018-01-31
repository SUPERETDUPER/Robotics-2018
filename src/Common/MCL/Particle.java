package Common.MCL;

import EV3.navigation.Readings;
import com.sun.istack.internal.NotNull;
import lejos.robotics.navigation.Pose;

/**
 * Immutable
 */
public final class Particle {
    private static final String LOG_TAG = Particle.class.getSimpleName();


    private final Pose pose;
    private final float weight;

    public Particle(float x, float y, float heading, float weight) {
        this.pose = new Pose(x, y, heading);
        this.weight = weight;
    }

    public Particle(float x, float y, float heading, Readings reading) {
        this.pose = new Pose(x, y, heading);
        this.weight = reading.calculateWeight(pose);
    }

    public Particle(@NotNull Pose pose, float weight) {
        this(pose.getX(), pose.getY(), pose.getHeading(), weight); // Not directly this.pose = pose because want to keep object immutable
    }

    public Particle(@NotNull Pose pose, Readings reading) {
        this(pose, reading.calculateWeight(pose));
    }

    public Pose getPose() {
        return new Pose(pose.getX(), pose.getY(), pose.getHeading()); //To make it immutable
    }

    public float getWeight() {
        return weight;
    }
}