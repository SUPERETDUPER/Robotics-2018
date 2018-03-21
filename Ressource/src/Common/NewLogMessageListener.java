/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common;

/**
 * Called when the there is a new log message
 */
public interface NewLogMessageListener {
    void sendLogMessage(String message);
}
