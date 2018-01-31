package EV3.navigation;

import lejos.robotics.navigation.Pose;

public interface Readings {
    float calculateWeight(Pose pose);
}
