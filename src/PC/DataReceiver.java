/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC;

import Common.EventTypes;
import Common.Logger;
import PC.GUI.GUI;

import java.io.DataInputStream;
import java.io.IOException;

final class DataReceiver {
    private static final String LOG_TAG = DataReceiver.class.getSimpleName();

    static void monitorForData(DataInputStream dis) throws IOException {
        //noinspection InfiniteLoopStatement
        for (; ; Thread.yield()) {
            DataReceiver.read(dis);
        }
    }


    private synchronized static void read(DataInputStream dis) throws IOException {
        EventTypes dataType = EventTypes.values()[dis.readByte()];

        switch (dataType) {
            case MCL_DATA:
                GUI.updateMCLData(dis);
                break;
            case LOG:
                System.out.println(dis.readUTF());
                break;
            case PATH:
                GUI.updatePaths(dis);
                break;
            default:
                Logger.error(LOG_TAG, "Not a recognized event type");
        }
    }
}