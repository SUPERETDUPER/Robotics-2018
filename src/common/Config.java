/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common;

import common.Logger.LogTypes;

/**
 * Environment specific and run specific settings
 */
public final class Config {
    public static final boolean DISPLAY_PARTICLE_WEIGHT = false;
    public final static float SIM_SPEED_FACTOR = 0.5F;
    public static final String IMAGE_PATH = "res/map.png";

    public enum Mode {
        SOLO,
        LINKED,
        SIM
    }

    public static final Mode currentMode = Mode.LINKED;

    public static final float GUI_DISPLAY_RATIO = 0.8F;
    public static final String EV3_IP_ADDRESS = "10.0.1.1";
    public static final int PORT_TO_CONNECT_ON_EV3 = 8888;

    static final LogTypes IMPORTANCE_TO_PRINT = LogTypes.DEBUG;
}
