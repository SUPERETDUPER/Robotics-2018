package Common.utils;

import Common.Config;
import EV3.DataSender;
import org.jetbrains.annotations.NotNull;

public final class Logger {

    private static final String ESCAPE_CHAR = "\u001B";

    private static final String ANSI_RESET = "[0m";
    private static final String ANSI_BLACK = "[30m";
    private static final String ANSI_BRIGHT_RED = "[1;31m";
    private static final String ANSI_BLUE = "[34m";
    private static final String ANSI_BRIGHT_YELLOW = "[33m";
    private static final String ANSI_BRIGHT_GREEN = "[1;32m";

    public enum LogTypes {
        ERROR,
        WARNING,
        INFO,
        DEBUG
    }

    private static void print(@NotNull LogTypes type, @NotNull String color, @NotNull String tag, @NotNull String message) {
        if (type.ordinal() <= Config.IMPORTANCE_TO_PRINT.ordinal()) {
            String toPrint =
                    ESCAPE_CHAR +
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

            if (!Config.useSimulator && DataSender.isConnected()) {
                DataSender.sendLogMessage(ESCAPE_CHAR +
                        ANSI_BRIGHT_GREEN +
                        "FROM DataSender : " +
                        toPrint);
            } else {
                System.out.println(toPrint);
            }
        }
    }

    public static void error(@NotNull String tag, @NotNull String message) {
        Logger.print(LogTypes.ERROR, ANSI_BRIGHT_RED, tag, message);
        throw new RuntimeException(message);
    }

    public static void warning(@NotNull String tag, @NotNull String message) {
        Logger.print(LogTypes.WARNING, ANSI_BRIGHT_YELLOW, tag, message);
    }

    public static void info(@NotNull String tag, @NotNull String message) {
        Logger.print(LogTypes.INFO, ANSI_BLUE, tag, message);
    }

    public static void debug(@NotNull String tag, @NotNull String message) {
        Logger.print(LogTypes.DEBUG, ANSI_BLACK, tag, message);
    }
}