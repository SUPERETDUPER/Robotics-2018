/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package imageuploader;

import common.Config;
import common.logger.Logger;
import lejos.hardware.Button;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;

public class EV3 {
    private static final String LOG_TAG = EV3.class.getSimpleName();

    public static void main(String[] args) {
        BufferedImage image = getBufferedImage();

        if (image == null) {
            return;
        }

        File file = new File(Config.EV3_IMAGE_PATH);
        System.out.println("Create file");

        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create new file");
        }

        try {
            ImageIO.write(image, "png", file);
            System.out.println("Done");
        } catch (IOException e) {
            System.out.println("Failed to createImageOutputStream");
        }

        Button.ENTER.waitForPress();
    }

    private static BufferedImage getBufferedImage() {
        BufferedImage image;

        try {
            System.out.println("Waiting connection");
            InputStream inputStream = new ServerSocket(Config.PORT_TO_CONNECT_ON_EV3).accept().getInputStream();
            System.out.println("Connected. Reading Img");
            image = ImageIO.read(inputStream);
            System.out.println("Got Image");
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed" + e);
            return null;
        }

        return image;
    }
}
