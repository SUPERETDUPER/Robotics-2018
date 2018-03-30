/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.TransmittableType;
import lejos.robotics.Transmittable;

public interface DataSender {
    void sendTransmittable(TransmittableType type, Transmittable data);

    void sendLogMessage(String message);

    void close();
}