/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common;

import Common.Logger.LogTypes;

/**
Environment specific and run specific settings
 */
public final class Config {

    public static final boolean DISPLAY_PARTICLE_WEIGHT = false;
    public final static int SIM_SPEED_REDUCING_FACTOR = 3;

    public static boolean runningOnEV3;

    public static final boolean useSimulator = true;
    public static final boolean usePC = true;

    public static final float GUI_DISPLAY_RATIO = 0.8F;
    public static final String EV3_IP_ADDRESS = "10.0.1.1";
    public static final int PORT_TO_CONNECT_ON_EV3 = 8888;

    static final LogTypes IMPORTANCE_TO_PRINT = LogTypes.DEBUG;
}
