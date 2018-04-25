/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import common.mapping.SurfaceMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;


class SurfaceMapLayer extends Layer {

    private static final BufferedImage surfaceMap = new SurfaceMap().getImage();

    SurfaceMapLayer() {
        super(surfaceMap.getWidth(), surfaceMap.getHeight());
    }

    @Override
    void displayOnGui(@NotNull GraphicsContext g) {
        g.drawImage(SwingFXUtils.toFXImage(surfaceMap, null), 0, 0);
    }

    @Override
    boolean shouldInvert() {
        return false;
    }
}
