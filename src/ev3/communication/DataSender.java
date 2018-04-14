/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.communication;

import common.TransmittableType;
import lejos.robotics.Transmittable;

interface DataSender {
    void sendTransmittable(TransmittableType type, Transmittable data);

    void close();

    interface LostConnectionListener {
        void lostConnection();
    }
}