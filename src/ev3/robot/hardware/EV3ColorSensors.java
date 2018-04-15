/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import common.logger.Logger;
import ev3.robot.Robot;
import lejos.hardware.sensor.EV3ColorSensor;
import org.jetbrains.annotations.Contract;

/**
 * class allowing access to ev3's color sensors
 *
 * The color sensors are created only when requested. In other words they are created the first time the getColorSensor___() is called.
 */
public final class EV3ColorSensors implements Robot.ColorSensors {
    private static final String LOG_TAG = EV3ColorSensors.class.getSimpleName();

    private volatile EV3ColorSensor sensorSurfaceLeft;
    private volatile EV3ColorSensor sensorSurfaceRight;
    private volatile EV3ColorSensor sensorContainer;
    private volatile EV3ColorSensor sensorBoat;

    private final Object surfaceLeftLock = new Object();
    private final Object surfaceRightLock = new Object();
    private final Object containerLock = new Object();
    private final Object boatLock = new Object();

    private Thread surfaceLeftCreatorThread;
    private Thread surfaceRightCreatorThread;
    private Thread containerCreatorThread;
    private Thread boatCreatorThread;

    /**
     * Starts 4 threads. One for each color sensor. The thread simply creates the sensor (which takes time).
     */
    @Override
    public void setup() {
        surfaceLeftCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (surfaceLeftLock) {
                    if (sensorSurfaceLeft == null) {
                        try {
                            sensorSurfaceLeft = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_LEFT);
                        } catch (IllegalArgumentException e) {
                            Logger.warning(LOG_TAG, "Could not create color sensor surface left");
                        }
                    }
                }
            }
        };

        surfaceRightCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (surfaceRightLock) {
                    if (sensorSurfaceRight == null) {
                        try {
                            sensorSurfaceRight = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_RIGHT);
                        } catch (IllegalArgumentException e) {
                            Logger.warning(LOG_TAG, "Could not create color sensor surface right");
                        }
                    }
                }
            }
        };

        containerCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (containerLock) {
                    if (sensorContainer == null) {
                        try {
                            sensorContainer = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_BLOCKS);
                        } catch (IllegalArgumentException e) {
                            Logger.warning(LOG_TAG, "Could not create color sensor for containers");
                        }
                    }
                }
            }
        };

        boatCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (boatLock) {
                    if (sensorBoat == null) {
                        try {
                            sensorBoat = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_BOAT);
                        } catch (IllegalArgumentException e) {
                            Logger.warning(LOG_TAG, "Could not create color sensor for boats");
                        }
                    }
                }
            }
        };

        surfaceLeftCreatorThread.start();
        surfaceRightCreatorThread.start();
        boatCreatorThread.start();
        containerCreatorThread.start();
    }

    @Contract(pure = true)
    @Override
    public boolean isSetup() {
        return !(boatCreatorThread.isAlive() || containerCreatorThread.isAlive() || surfaceLeftCreatorThread.isAlive() || surfaceRightCreatorThread.isAlive());
    }

    @Override
    public int getColorSurfaceLeft() {
        if (sensorSurfaceLeft == null) {
            synchronized (surfaceLeftLock) {
                if (sensorSurfaceLeft == null) {
                    sensorSurfaceLeft = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_LEFT);
                }
            }
        }

        return sensorSurfaceLeft.getColorID();
    }

    @Override
    public int getColorSurfaceRight() {
        if (sensorSurfaceRight == null) {
            synchronized (surfaceRightLock) {
                if (sensorSurfaceRight == null) {
                    sensorSurfaceRight = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_RIGHT);
                }
            }
        }

        return sensorSurfaceRight.getColorID();
    }

    @Override
    public int getColorContainer() {
        if (sensorContainer == null) {
            synchronized (containerLock) {
                if (sensorContainer == null) {
                    sensorContainer = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_BLOCKS);
                }
            }
        }

        return sensorContainer.getColorID();
    }

    @Override
    public int getColorBoat() {
        if (sensorBoat == null) {
            synchronized (boatLock) {
                if (sensorBoat == null) {
                    sensorBoat = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_BOAT);
                }
            }
        }

        return sensorBoat.getColorID();
    }
}