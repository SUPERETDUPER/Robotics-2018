/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

public class DataListener {
    private final DataSender sender;

    public DataListener(DataSender sender) {
        this.sender = sender;
    }
}
