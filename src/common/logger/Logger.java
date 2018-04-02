/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.logger;

import common.Config;
import org.jetbrains.annotations.NotNull;

/**
 * Prints to console log messages.
 * <p>
 * If listener is set instead of printing to console notifies the listener of new log messages
 */
public final class Logger {
    private static final String ANSI_BLACK = "[30m";
    private static final String ANSI_BRIGHT_RED = "[1;31m";
    private static final String ANSI_BLUE = "[34m";
    private static final String ANSI_BRIGHT_YELLOW = "[33m";

    public enum LogTypes {
        ERROR,
        WARNING,
        INFO,
        DEBUG
    }

    private static LogMessageListener listener;

    /**
     * Allows to set a custom listener
     *
     * @param listener the custom listener
     */
    public static void setListener(LogMessageListener listener) {
        Logger.listener = listener;
    }

    public static void removeListener() {
        listener = null;
    }

    /**
     * If running on ev3 and using pc send to pc. Else print on brick
     *
     * @param message message to be sent
     */
    private static void print(@NotNull LogTypes type, @NotNull String color, @NotNull String tag, @NotNull String message) {
        if (type.ordinal() <= Config.IMPORTANCE_TO_PRINT.ordinal()) {
            LogMessage logMessage = new LogMessage(type, color, tag, message);

            if (listener == null) {
                logMessage.printToSysOut(null);
            } else {
                listener.notifyLogMessage(logMessage);
            }
        }
    }

    public static void error(@NotNull String tag, @NotNull String message) {
        print(LogTypes.ERROR, ANSI_BRIGHT_RED, tag, message);
    }

    public static void warning(@NotNull String tag, @NotNull String message) {
        print(LogTypes.WARNING, ANSI_BRIGHT_YELLOW, tag, message);
    }

    public static void info(@NotNull String tag, @NotNull String message) {
        print(LogTypes.INFO, ANSI_BLUE, tag, message);
    }

    @SuppressWarnings("unused")
    public static void debug(@NotNull String tag, @NotNull String message) {
        print(LogTypes.DEBUG, ANSI_BLACK, tag, message);
    }
}