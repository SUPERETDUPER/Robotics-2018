/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.logger;

import lejos.robotics.Transmittable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Stores a log message
 * TODO : Refactor so that the log message stores the data separately allowing it to print without color codes (for the EV3 screen). Note : also store thread
 */
public class LogMessage implements Transmittable {
    //Constants for making log messages colored
    private static final String ESCAPE_CHAR = "\u001B";
    private static final String ANSI_RESET = "[0m";

    private String message;

    public LogMessage() {
    }

    LogMessage(@NotNull Logger.LogTypes type, @NotNull String colorConstant, @NotNull String tag, @NotNull String message) {
        this.message = ESCAPE_CHAR +
                colorConstant +
                type.name().toUpperCase() +
                ": " +
                Thread.currentThread().getName() +
                ": " +
                tag +
                ": " +
                message +
                ESCAPE_CHAR +
                ANSI_RESET;
    }

    @Override
    public void dumpObject(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(message);
    }

    @Override
    public void loadObject(DataInputStream dataInputStream) throws IOException {
        message = dataInputStream.readUTF();
    }

    public void printToSysOut(){
        printToSysOut("");
    }

    public void printToSysOut(@Nullable String prefix) {
        if (prefix == null) {
            System.out.println(message);
        } else {
            System.out.println(prefix + message);
        }
    }
}
