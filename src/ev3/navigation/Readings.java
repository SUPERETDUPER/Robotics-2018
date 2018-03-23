/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.navigation;

import lejos.robotics.navigation.Pose;

public interface Readings {
    float calculateWeight(Pose pose);
}
