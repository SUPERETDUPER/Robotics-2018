/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.logger;

import common.Config;
import org.jetbrains.annotations.NotNull;

/**
 * Called throughout the program to print log message
 * Prints the log message to System.out unless a listener is set in which case log messages get sent to the listener
 */
public final class
Logger {
    //Constants to make messages colorful
    private static final String ANSI_BLACK = "[30m";
    private static final String ANSI_BRIGHT_RED = "[1;31m";
    private static final String ANSI_BLUE = "[34m";
    private static final String ANSI_BRIGHT_YELLOW = "[33m";

    //Priority levels *in order*
    public enum LogTypes {
        DEBUG,
        INFO,
        WARNING,
        ERROR
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

    // LOGGING METHODS //
    @SuppressWarnings("unused")
    public static void error(@NotNull String tag, @NotNull String message) {
        print(LogTypes.ERROR, ANSI_BRIGHT_RED, tag, message);
    }

    @SuppressWarnings("unused")
    public static void warning(@NotNull String tag, @NotNull String message) {
        print(LogTypes.WARNING, ANSI_BRIGHT_YELLOW, tag, message);
    }

    @SuppressWarnings("unused")
    public static void info(@NotNull String tag, @NotNull String message) {
        print(LogTypes.INFO, ANSI_BLUE, tag, message);
    }

    @SuppressWarnings("unused")
    public static void debug(@NotNull String tag, @NotNull String message) {
        print(LogTypes.DEBUG, ANSI_BLACK, tag, message);
    }

    /**
     * Creates a log message then prints it our send it to the listener
     */
    private static void print(@NotNull LogTypes type, @NotNull String color, @NotNull String tag, @NotNull String message) {
        //Only print if priority above limit
        if (type.ordinal() >= Config.IMPORTANCE_TO_PRINT.ordinal()) {
            LogMessage logMessage = new LogMessage(type, color, tag, message);

            if (listener == null) {
                logMessage.printToSysOut();
            } else {
                listener.notifyLogMessage(logMessage);
            }
        }
    }
}