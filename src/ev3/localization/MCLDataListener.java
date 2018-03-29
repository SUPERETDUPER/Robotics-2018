/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.particles.MCLData;

public interface MCLDataListener {
    void notifyNewMCLData(MCLData data);
}
