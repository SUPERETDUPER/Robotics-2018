/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common;

/**
 * Enum used to indicate which type of data is being sent and to provide a way for the receiver to recognize the data
 */
public enum TransmittableType {
    MCL_DATA,
    LOG,
    PATH,
    CURRENT_POSE
}
