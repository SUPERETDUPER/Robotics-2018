package Common.MCL;

import com.sun.istack.internal.NotNull;
import lejos.robotics.navigation.Pose;

/**
 * Immutable
 */
public final class Particle {
    private static final String LOG_TAG = Particle.class.getSimpleName();
    private static final float DEFAULT_WEIGHT = 1;

    private final Pose pose;
    private final float weight;

    public Particle(float x, float y, float heading, float weight) {
        this.pose = new Pose(x, y, heading);
        this.weight = weight;
    }

    public Particle(@NotNull Pose pose, float weight) {
        this(pose.getX(), pose.getY(), pose.getHeading(), weight); // Not directly this.pose = pose because want to keep object immutable
    }

    public Particle(Pose pose) {
        this(pose, DEFAULT_WEIGHT);
    }

    public Particle(float x, float y, float heading) {
        this(x, y, heading, DEFAULT_WEIGHT);
    }

    public Pose getPose() {
        return new Pose(pose.getX(), pose.getY(), pose.getHeading()); //To make it immutable
    }

    public float getWeight() {
        return weight;
    }
}