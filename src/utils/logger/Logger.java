package utils.logger;


public final class Logger {

    private static final String ESCAPE_CHAR = "\u001B";

    private static final String ANSI_RESET = "[0m";

    private static final String ANSI_BLACK = "[30m";
    private static final String ANSI_BRIGHT_RED = "[1;31m";
    private static final String ANSI_BLUE = "[34m";
    private static final String ANSI_BRIGHT_YELLOW = "[1;33m";

    public static final LoggerType typeError = new LoggerType("Error", 0, ANSI_BRIGHT_RED);
    public static final LoggerType typeWarning = new LoggerType("Warning", 1, ANSI_BRIGHT_YELLOW);
    public static final LoggerType typeInfo = new LoggerType("Info", 2, ANSI_BLUE);
    public static final LoggerType typeDebug = new LoggerType("Debug", 3, ANSI_BLACK);

    private static final int IMPORTANCE_TO_PRINT = typeDebug.getImportance();

    public static void print(LoggerType type, String tag, String message) {
        if (shouldPrint(type)) {
            String toPrint = "";

            toPrint += ESCAPE_CHAR + type.getColor();
            toPrint += type.getName().toUpperCase();
            toPrint += " : " + tag;
            toPrint += " : " + message;
            toPrint += ESCAPE_CHAR + ANSI_RESET;

            System.out.println(toPrint);
        }
    }

    private static boolean shouldPrint(LoggerType type) {
        return type.getImportance() <= IMPORTANCE_TO_PRINT;
    }
}