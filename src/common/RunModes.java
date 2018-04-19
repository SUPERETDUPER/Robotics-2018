/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common;

/**
 * The different modes that are available
 * SOLO : EV3 runs without a computer. Used during competition
 * DUAL : EV3 runs with a computer. Used to view the EV3 states on the computer's screen.
 * SIM : The computer simulates an EV3 that is connected to the computer. Same as DUAL but without the real EV3.
 */
public enum RunModes {
    SOLO,
    DUAL,
    SIM
}
