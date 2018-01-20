package utils;


import com.sun.istack.internal.NotNull;

public final class Logger {

    private static final String ESCAPE_CHAR = "\u001B";

    private static final String ANSI_RESET = "[0m";
    private static final String ANSI_BLACK = "[30m";
    private static final String ANSI_BRIGHT_RED = "[1;31m";
    private static final String ANSI_BLUE = "[34m";
    private static final String ANSI_BRIGHT_YELLOW = "[1;33m";

    private static void print(@NotNull LogTypes type, @NotNull String color, @NotNull String tag, @NotNull String message) {
        if (type.ordinal() <= Config.IMPORTANCE_TO_PRINT) {
            String toPrint = "";

            toPrint += ESCAPE_CHAR + color;
            toPrint += type.name().toUpperCase();
            toPrint += " : " + tag;
            toPrint += " : " + message;
            toPrint += ESCAPE_CHAR + ANSI_RESET;

            System.out.println(toPrint);
        }
    }

    public static void error(@NotNull String tag, @NotNull String message) {
        Logger.print(LogTypes.ERROR, ANSI_BRIGHT_RED, tag, message);
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

    private enum LogTypes {
        ERROR,
        WARNING,
        INFO,
        DEBUG
    }
}