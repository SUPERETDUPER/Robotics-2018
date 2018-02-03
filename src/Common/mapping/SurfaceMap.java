/*
 * MIT License
 *
 * Copyright (c) [2018] [Martin Staadecker]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package Common.mapping;

import PC.GUI.Displayable;
import lejos.robotics.Color;
import lejos.robotics.geometry.Point;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SurfaceMap extends RegionContainer implements Displayable {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    private static final Rectangle boundingRectangle = new Rectangle(Color.WHITE, 0, 0, 2362, 1143) {
        @Override
        public void displayOnGui(@NotNull Graphics g) {
            g.setColor(java.awt.Color.LIGHT_GRAY); //So that we see difference in GUI
            g.fillRect((int) mRectangle.x, (int) mRectangle.y, (int) mRectangle.width, (int) mRectangle.height);
        }
    };


    private static final ArrayList<ColoredRegion> regions = new ArrayList<>();

    static {
        regions.add(boundingRectangle);
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

    private static final SurfaceMap mSurfaceMap = new SurfaceMap();

    private SurfaceMap() {
        super(regions);
    }

    @NotNull
    @Contract(pure = true)
    public static SurfaceMap get() {
        return mSurfaceMap;
    }

    @NotNull
    public static Point getRandomPoint() {
        return new Point(
                boundingRectangle.getX1() + (float) (Math.random()) * boundingRectangle.getWidth(),
                boundingRectangle.getY1() + (float) (Math.random()) * boundingRectangle.getHeight()
        );
    }

    @Override
    public boolean contains(@NotNull Point point) {
        return boundingRectangle.contains(point);
    }
}