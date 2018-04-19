/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.robot.hardware;

import common.logger.Logger;
import ev3.robot.Robot;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import org.jetbrains.annotations.Contract;

/**
 * class allowing access to ev3's color sensors
 *
 * The color sensors are created only when requested. In other words they are created the first time the getColorSensor___() is called.
 */
public final class EV3ColorSensors implements Robot.ColorSensors {
    private static final String LOG_TAG = EV3ColorSensors.class.getSimpleName();

    private volatile SampleProvider sampleProviderSurfaceLeft;
    private volatile SampleProvider sampleProviderSurfaceRight;
    private volatile SampleProvider sampleProviderContainer;
    private volatile SampleProvider sampleProviderBoat;

    private final Object surfaceLeftLock = new Object();
    private final Object surfaceRightLock = new Object();
    private final Object containerLock = new Object();
    private final Object boatLock = new Object();

    private Thread surfaceLeftCreatorThread;
    private Thread surfaceRightCreatorThread;
    private Thread containerCreatorThread;
    private Thread boatCreatorThread;

    private float[] sampleSurfaceLeft;
    private float[] sampleSurfaceRight;
    private float[] sampleContainer;
    private float[] sampleBoat;

    /**
     * Starts 4 threads. One for each color sensor. The thread simply creates the sensor (which takes time).
     */
    @Override
    public void setup() {
        surfaceLeftCreatorThread = new Thread() {
            @Override
            public void run() {
                synchronized (surfaceLeftLock) {
                    if (sampleProviderSurfaceLeft == null) {
                        try {
                            sampleProviderSurfaceLeft = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_LEFT).getColorIDMode();
                            sampleSurfaceLeft = new float[sampleProviderSurfaceLeft.sampleSize()];
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
                    if (sampleProviderSurfaceRight == null) {
                        try {
                            sampleProviderSurfaceRight = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_RIGHT).getColorIDMode();
                            sampleSurfaceRight = new float[sampleProviderSurfaceRight.sampleSize()];
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
                    if (sampleProviderContainer == null) {
                        try {
                            sampleProviderContainer = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_BLOCKS).getColorIDMode();
                            sampleContainer = new float[sampleProviderContainer.sampleSize()];
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
                    if (sampleProviderBoat == null) {
                        try {
                            sampleProviderBoat = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_BOAT).getColorIDMode();
                            sampleBoat = new float[sampleProviderBoat.sampleSize()];
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
        if (sampleProviderSurfaceLeft == null) {
            synchronized (surfaceLeftLock) {
                if (sampleProviderSurfaceLeft == null) {
                    sampleProviderSurfaceLeft = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_LEFT).getColorIDMode();
                }
            }
        }

        sampleProviderSurfaceLeft.fetchSample(sampleSurfaceLeft, 0);

        return (int) sampleSurfaceLeft[0];
    }

    @Override
    public int getColorSurfaceRight() {
        if (sampleProviderSurfaceRight == null) {
            synchronized (surfaceRightLock) {
                if (sampleProviderSurfaceRight == null) {
                    sampleProviderSurfaceRight = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_SURFACE_RIGHT).getColorIDMode();
                }
            }
        }

        sampleProviderSurfaceRight.fetchSample(sampleSurfaceRight, 0);

        return (int) sampleSurfaceRight[0];
    }

    @Override
    public int getColorContainer() {
        if (sampleProviderContainer == null) {
            synchronized (containerLock) {
                if (sampleProviderContainer == null) {
                    sampleProviderContainer = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_BLOCKS).getColorIDMode();
                }
            }
        }

        sampleProviderContainer.fetchSample(sampleContainer, 0);

        return (int) sampleContainer[0];
    }

    @Override
    public int getColorBoat() {
        if (sampleProviderBoat == null) {
            synchronized (boatLock) {
                if (sampleProviderBoat == null) {
                    sampleProviderBoat = new EV3ColorSensor(Ports.PORT_SENSOR_COLOR_BOAT).getColorIDMode();
                }
            }
        }

        sampleProviderBoat.fetchSample(sampleBoat, 0);

        return (int) sampleBoat[0];
    }
}