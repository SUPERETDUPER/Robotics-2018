/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common;

import org.jetbrains.annotations.NotNull;

/**
 * Prints to console log messages.
 *
 * If listener is set instead of printing to console notifies the listener of new log messages
 */
public final class Logger {
    private static final String ESCAPE_CHAR = "\u001B";

    private static final String ANSI_RESET = "[0m";
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
     * @param listener the custom listener
     */
    public static void setListener(LogMessageListener listener) {
        Logger.listener = listener;
    }

    public static void removeListener(){
        listener = null;
    }

    @NotNull
    private static String constructMessage(@NotNull LogTypes type, @NotNull String color, @NotNull String tag, @NotNull String message) {
        return ESCAPE_CHAR +
                color +
                type.name().toUpperCase() +
                " : " +
                Thread.currentThread().getName() +
                " : " +
                tag +
                " : " +
                message +
                ESCAPE_CHAR +
                ANSI_RESET;
    }

    /**
     * If running on ev3 and using pc send to pc. Else print on brick
     *
     * @param message message to be sent
     * @param type    type of the message. Used to check if should be printed
     */
    private static void print(String message, @NotNull LogTypes type) {
        if (type.ordinal() <= Config.IMPORTANCE_TO_PRINT.ordinal()) {
            if (listener == null){
                System.out.println(message);
            } else {
                listener.notifyLogMessage(message);
            }
        }
    }

    public static void error(@NotNull String tag, @NotNull String message) {
        print(constructMessage(LogTypes.ERROR, ANSI_BRIGHT_RED, tag, message), LogTypes.ERROR);
    }

    public static void warning(@NotNull String tag, @NotNull String message) {
        print(constructMessage(LogTypes.WARNING, ANSI_BRIGHT_YELLOW, tag, message), LogTypes.WARNING);
    }

    public static void info(@NotNull String tag, @NotNull String message) {
        print(constructMessage(LogTypes.INFO, ANSI_BLUE, tag, message), LogTypes.INFO);
    }

    public static void debug(@NotNull String tag, @NotNull String message) {
        print(constructMessage(LogTypes.DEBUG, ANSI_BLACK, tag, message), LogTypes.DEBUG);
    }
}