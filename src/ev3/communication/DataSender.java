/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.TransmittableType;
import lejos.robotics.Transmittable;

public interface DataSender {
    public void sendTransmittable(TransmittableType type, Transmittable data);
}