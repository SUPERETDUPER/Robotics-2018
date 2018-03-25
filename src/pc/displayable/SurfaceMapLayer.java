/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.displayable;

import common.mapping.SurfaceMap;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;


public class SurfaceMapLayer extends Layer {
    @Override
    public void displayOnGui(@NotNull GraphicsContext g) {
        g.drawImage(SurfaceMap.getImage(), 0, 0);
    }

    @Override
    public boolean invert() {
        return false;
    }
}
