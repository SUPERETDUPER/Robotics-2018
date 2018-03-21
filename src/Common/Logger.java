/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package Common;

import EV3.DataSender;
import org.jetbrains.annotations.NotNull;

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

    private static String constructMessage(@NotNull LogTypes type, @NotNull String color, @NotNull String tag, @NotNull String message) {
        return ESCAPE_CHAR +
                color +
                (Config.runningOnEV3 ? "EV3 : " : "PC : ") +
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
     * If using PC and not sim sends all log messages to PC. Else print on brick
     *
     * @param message message to be sent
     * @param type    type of the message. Used to check if should be printed
     */
    private static void print(String message, @NotNull LogTypes type) {
        if (type.ordinal() <= Config.IMPORTANCE_TO_PRINT.ordinal()) {
            if (Config.runningOnEV3 && !Config.useSimulator) {
                DataSender.sendLogMessage(message);
            } else {
                System.out.println(message);
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