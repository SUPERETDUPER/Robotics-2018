/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package common.mapping;

import common.logger.Logger;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SurfaceMap {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    @NotNull
    private final BufferedImage image;

    public SurfaceMap(String filePath) {
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Unable to read picture");
            throw new RuntimeException(e.toString());
        }
    }

    @NotNull
    public BufferedImage getImage() {
        return image;
    }
}
