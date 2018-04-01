/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import lejos.robotics.Transmittable;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * A special type of layer that can be updated with a data output stream. Only gets drawn if there is new data.
 */
public abstract class UpdatableLayer extends Layer {
    private boolean dataNew = true;

    public UpdatableLayer(int width, int height) {
        super(width, height);
    }

    public void update(DataInputStream dataInputStream) throws IOException {
        getContent().loadObject(dataInputStream);
        dataNew = true;
    }

    abstract Transmittable getContent();

    @Override
    public synchronized void draw() {
        if (dataNew) {
            super.draw();
            dataNew = false;
        }
    }
}
