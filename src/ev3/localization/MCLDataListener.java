/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.particles.ParticleAndPoseContainer;

public interface MCLDataListener {
    void notifyNewMCLData(ParticleAndPoseContainer data);
}
