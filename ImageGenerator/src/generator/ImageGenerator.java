/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package generator;

import Common.Config;
import Common.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import lejos.robotics.Color;
import lejos.robotics.geometry.Point;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageGenerator {
    private static final String LOG_TAG = ImageGenerator.class.getSimpleName();

    private static final Rectangle boundingRectangle = new Rectangle(Color.WHITE, 0, 0, 2362, 1143);

    private static final ArrayList<ColorRegion> regions = new ArrayList<>();

    static {
        regions.add(new Rectangle(Color.BLUE, 0, 0, 412.5F, 1143));

        //Vertical lines
        regions.add(new Rectangle(Color.BLACK, 180, 0, 20, 1143));
        regions.add(new Rectangle(Color.BLACK, 412.5F, 0, 20, 1143));
        regions.add(new Rectangle(Color.BLACK, 655.5F, 138, 20, 867));
        regions.add(new Rectangle(Color.BLACK, 1044.5F, 0, 20, 1143));
        regions.add(new Rectangle(Color.BLACK, 1722.5F, 0, 20, 1143));
        regions.add(new Rectangle(Color.BLACK, 2222, 0, 20, 1143));

        //Horizontal lines
        regions.add(new Rectangle(Color.BLACK, 432.5F, 566, 223, 20));
        regions.add(new Rectangle(Color.BLACK, 655.5F, 138, 1582, 20));
        regions.add(new Rectangle(Color.BLACK, 655.5F, 1005, 1582, 20));

        //Temp reg area lines
        regions.add(new Rectangle(Color.BLACK, 1610.5F, 294, 243, 20));
        regions.add(new Rectangle(Color.BLACK, 1610.5F, 828, 243, 20));

        //Temp reg area base
        regions.add(new Rectangle(Color.YELLOW, 1530.5F, 274, 80, 64));
        regions.add(new Rectangle(Color.BLUE, 1853, 274, 80, 64));
        regions.add(new Rectangle(Color.RED, 1530.5F, 807, 80, 64));
        regions.add(new Rectangle(Color.GREEN, 1853, 807, 80, 64));

        //TODO Make more precise, slightly off
        //Container lines
        regions.add(new Polygon(Color.BLACK, Arrays.asList(
                new Point(1328.4F, 272),
                new Point(1342.5F, 286.1F),
                new Point(841.5F, 797.1F),
                new Point(827.4F, 783)
        )));

        regions.add(new Polygon(Color.BLACK, Arrays.asList(
                new Point(896.4F, 431),
                new Point(910.5F, 416.9F),
                new Point(1207.6F, 732),
                new Point(1193.5F, 746.1F)
        )));

        //Containers base
        regions.add(new Rectangle(Color.WHITE, 822.5F, 343, 88, 88)); //Top-left
        regions.add(new Rectangle(Color.WHITE, 1254.5F, 272, 88, 88)); //Top-right
        regions.add(new Rectangle(Color.WHITE, 753.5F, 783, 88, 88)); //Bottom-left
        regions.add(new Rectangle(Color.WHITE, 1193.5F, 732, 88, 88)); //Bottom-right

        //Starting area
        regions.add(new Rectangle(Color.GREEN, 2102, 435, 260, 270));
        regions.add(new Rectangle(Color.WHITE, 2112, 445, 250, 250));

        //Boats
        for (int i = 0; i < 6; i++) {
            regions.add(new Rectangle(Color.WHITE, 212.5F, 73 + 182.6F * i, 116, 84));
            regions.add(new Rectangle(Color.BLACK, 328.5F, 105 + 182.6F * i, 84, 20));
        }
    }

    private static javafx.scene.paint.Color getDisplayColor(Point point, ArrayList<ColorRegion> regions) {
        javafx.scene.paint.Color colorUnderPoint = boundingRectangle.getDisplayColor();

        for (ColorRegion region : regions) {
            if (region.contains(point)) {
                colorUnderPoint = region.getDisplayColor();
            }
        }

        return colorUnderPoint;
    }

    private static void generateImage(ArrayList<ColorRegion> regions) {
        WritableImage image = new WritableImage((int) boundingRectangle.getWidth(), (int) boundingRectangle.getHeight());
        PixelWriter pixelWriter = image.getPixelWriter();

        for (int x = 0; x < boundingRectangle.getWidth(); x++) {
            for (int y = 0; y < boundingRectangle.getHeight(); y++) {
                pixelWriter.setColor(x, y, getDisplayColor(new Point(x, y), regions));
            }
        }

        try {
            File file = new File(Config.IMAGE_PATH);
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            Logger.error(LOG_TAG, "Failed to write Image to file" + e);
        }
    }

    public static void main(String[] args) {
        generateImage(regions);
    }
}