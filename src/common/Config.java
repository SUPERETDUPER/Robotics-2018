/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common;

import common.logger.Logger.LogTypes;

/**
 * Environment specific and run specific settings
 */
public final class Config {

    /**
     * The current mode to run in. (See {@link RunModes})
     **/
    public static final RunModes currentMode = RunModes.SIM;

    public static final float SIM_SPEED_FACTOR = 1F; //How much the sim is slowed down

    public static final boolean SHOW_PARTICLE_TAILS = true; //Whether the GUI should show the particles heading with a "tail"

    public static final float GUI_DISPLAY_RATIO = 0.8F; //How much to zoom or shrink the display. To adjust based on screen size.
    public static final String EV3_IP_ADDRESS = "10.0.1.1"; //EV3's ip address. 10.0.1.1 is the default.
    public static final int PORT_TO_CONNECT_ON_EV3 = 8888; //Port to use for connection. Any open port works

    public static final LogTypes IMPORTANCE_TO_PRINT = LogTypes.DEBUG; //Sets the minimum priority the Logger should print

    public static final boolean WAIT_FOR_SENSORS = false; //Determines whether the program should start immediately or first create all the sensors
    public static final String MAP_PATH = "/map.png";
}
