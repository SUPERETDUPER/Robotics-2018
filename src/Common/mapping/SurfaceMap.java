package Common.mapping;

import PC.GUI.Displayable;
import lejos.robotics.Color;
import lejos.robotics.geometry.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SurfaceMap extends RegionContainer implements Displayable {
    private static final String LOG_TAG = SurfaceMap.class.getSimpleName();

    private static final Rectangle boundingRectangle = new Rectangle(Color.WHITE, 0, 0, 2362, 1143){
        @Override
        public void displayOnGui(Graphics g) {
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
        regions.add(new Rectangle(Color.BLACK, 2222, 0, 20, 435));
        regions.add(new Rectangle(Color.BLACK, 2222, 708, 20, 435));

        //Horizontal lines
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

        //Boats
        for (int i = 0 ; i < 6  ; i++) {
            regions.add(new Rectangle(Color.WHITE, 212.5F, 73 + 182.6F * i, 116, 84));
            regions.add(new Rectangle(Color.BLACK, 328.5F, 105 + 182.6F * i, 84, 20));
        }

        //TODO Finish designing mat
    }

    private static final SurfaceMap mSurfaceMap = new SurfaceMap();

    private SurfaceMap() {
        super(regions);
    }

    public static SurfaceMap get() {
        return mSurfaceMap;
    }

    public static Point getRandomPoint() {
        return new Point(
                boundingRectangle.getX1() + (float) (Math.random()) * boundingRectangle.getWidth(),
                boundingRectangle.getY1() + (float) (Math.random()) * boundingRectangle.getHeight()
        );
    }

    @Override
    public boolean contains(Point point) {
        return boundingRectangle.contains(point);
    }
}