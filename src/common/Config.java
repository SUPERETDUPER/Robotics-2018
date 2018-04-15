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
     * The different modes that are available
     * SOLO : EV3 runs without a computer. Used during competition
     * DUAL : EV3 runs with a computer. Used to view the EV3 states on the computer's screen.
     * SIM : The computer simulates an EV3 that is connected to the computer. Same as DUAL but without the real EV3.
     */
    public enum Mode {
        SOLO,
        DUAL,
        SIM
    }

    public static final Mode currentMode = Mode.DUAL; //The current mode to run in

    public static final float SIM_SPEED_FACTOR = 0.7F; //How much the sim is slowed down

    //Paths to map file
    public static final String PC_IMAGE_PATH = "res/map.png";
    public static final String EV3_IMAGE_PATH = "/map.png";

    public static final boolean SHOW_PARTICLE_TAILS = true; //Whether the GUI should show the particles heading with a "tail"

    public static final float GUI_DISPLAY_RATIO = 0.8F; //How much to zoom or shrink the display. To adjust based on screen size.
    public static final String EV3_IP_ADDRESS = "10.0.1.1"; //EV3's ip address. 10.0.1.1 is the default.
    public static final int PORT_TO_CONNECT_ON_EV3 = 8888; //Port to use for connection. Any open port works

    public static final LogTypes IMPORTANCE_TO_PRINT = LogTypes.DEBUG; //Sets the minimum priority the Logger should print

    public static final boolean WAIT_FOR_SENSORS = false; //Determines whether the program should start immediately or first create all the sensors
}
