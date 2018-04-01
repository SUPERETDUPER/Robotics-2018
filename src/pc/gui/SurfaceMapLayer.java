/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;


public class SurfaceMapLayer extends Layer {

    private final BufferedImage surfaceMap;

    public SurfaceMapLayer(BufferedImage surfaceMap) {
        super(surfaceMap.getWidth(), surfaceMap.getHeight());

        this.surfaceMap = surfaceMap;
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
