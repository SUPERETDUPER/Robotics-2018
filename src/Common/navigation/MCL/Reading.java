package Common.navigation.MCL;

import lejos.robotics.navigation.Pose;

public interface Reading {
    float calculateWeight(Pose pose);
}
