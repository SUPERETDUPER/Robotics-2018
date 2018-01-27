package Common.navigation.MCL;

import lejos.robotics.navigation.Pose;

public interface Readings {
    float calculateWeight(Pose pose);
}
