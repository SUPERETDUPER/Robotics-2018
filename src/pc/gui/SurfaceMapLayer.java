/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import common.mapping.SurfaceMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;


public class SurfaceMapLayer extends Layer {

    private final SurfaceMap surfaceMap;

    public SurfaceMapLayer(SurfaceMap surfaceMap) {
        super(surfaceMap.getWidth(), surfaceMap.getHeight());

        this.surfaceMap = surfaceMap;
    }

    @Override
    void displayOnGui(@NotNull GraphicsContext g) {
        g.drawImage(SwingFXUtils.toFXImage(surfaceMap.getImage(), null), 0, 0);
    }

    @Override
    boolean shouldInvert() {
        return false;
    }
}
