/*
 * MIT License
 *
 * Copyright (c) [2018] [Martin Staadecker]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package Common.utils;

import Common.Config;
import EV3.DataSender;
import org.jetbrains.annotations.NotNull;

public final class Logger {

    private static final String LOG_TAG = Logger.class.getSimpleName();

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

    private static void print(String message, @NotNull LogTypes type) {
        if (type.ordinal() <= Config.IMPORTANCE_TO_PRINT.ordinal()) {
            if (Config.usePC) {
                switch (Config.runningOn) {
                    case PC:
                        System.out.println("PC : " + message);
                        break;
                    case EV3:
                        DataSender.sendLogMessage("EV3: " + message);
                        break;
                    default:
                        System.out.println(constructMessage(LogTypes.ERROR, ANSI_BRIGHT_RED, LOG_TAG, "Error running on unknown enum type"));
                }
            } else {
                System.out.println(message);
            }
        }
    }

    public static void error(@NotNull String tag, @NotNull String message) {
        print(constructMessage(LogTypes.ERROR, ANSI_BRIGHT_RED, tag, message), LogTypes.ERROR);
        throw new RuntimeException(message);
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