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

public class LogMessage implements Transmittable {
    private static final String ESCAPE_CHAR = "\u001B";
    private static final String ANSI_RESET = "[0m";

    private String message;

    public LogMessage() {
    }

    LogMessage(@NotNull Logger.LogTypes type, @NotNull String color, @NotNull String tag, @NotNull String message) {
        this.message = ESCAPE_CHAR +
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

    @Override
    public void dumpObject(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(message);
    }

    @Override
    public void loadObject(DataInputStream dataInputStream) throws IOException {
        message = dataInputStream.readUTF();
    }

    public void printToSysOut(@Nullable String prefix) {
        if (prefix == null) {
            System.out.println(message);
        } else {
            System.out.println(prefix + message);
        }
    }
}
