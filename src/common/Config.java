/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common;

import common.logger.Logger.LogTypes;

/**
 * Environment specific and run specific settings
 */
public final class Config {
    public static final float SIM_SPEED_FACTOR = 0.7F;
    public static final String PC_IMAGE_PATH = "res/map.png";
    public static final String EV3_IMAGE_PATH = "/map.png";
    public static final boolean SHOW_PARTICLE_TAILS = true;

    public enum Mode {
        SOLO,
        DUAL,
        SIM
    }

    public static final Mode currentMode = Mode.SIM;

    public static final float GUI_DISPLAY_RATIO = 0.8F;
    public static final String EV3_IP_ADDRESS = "10.0.1.1";
    public static final int PORT_TO_CONNECT_ON_EV3 = 8888;

    public static final LogTypes IMPORTANCE_TO_PRINT = LogTypes.DEBUG;
}
