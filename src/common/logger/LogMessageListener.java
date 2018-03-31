/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.logger;

/**
 * Called when the there is a new log message
 */
public interface LogMessageListener {
    void notifyLogMessage(LogMessage message);
}
