/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common;

/**
 * Called when the there is a new log message
 */
public interface NewEV3LogMessageListener {
    void notifyNewEV3Message(String message);
}
