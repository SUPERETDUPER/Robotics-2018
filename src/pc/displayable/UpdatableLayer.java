/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.displayable;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class UpdatableLayer extends Layer {
    private boolean dataNew = true;

    public abstract void updateLayer(DataInputStream dataInputStream) throws IOException;

    public void markNew() {
        dataNew = true;
    }

    public boolean hasNewData() {
        return dataNew;
    }

    @Override
    public synchronized void draw() {
        super.draw();

        dataNew = false;
    }
}
