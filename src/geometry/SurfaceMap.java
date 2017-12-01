package geometry;

import lejos.robotics.Color;
import lejos.robotics.geometry.Point;
import lejos.robotics.geometry.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SurfaceMap extends JComponent {
    private static final int DEFAULT_COLOR = Color.WHITE;
    public static final Rectangle BOUNDING_RECTANGLE = new Rectangle(0, 0, 2362F, 1143F);

    private ArrayList<ColoredRegion> regions = new ArrayList<>();

    private Rectangle boundingRectangle;

    public SurfaceMap(Rectangle boundingRectangle) {
        this(boundingRectangle, new ArrayList<ColoredRegion>());
    }

    public SurfaceMap(Rectangle boundingRectangle, ArrayList<ColoredRegion> regions) {
        this.boundingRectangle = boundingRectangle;
        this.regions = regions;
    }

    public boolean contains(Point point) {
        return boundingRectangle.contains(point);
    }

    public void addRegion(ColoredRegion coloredRegion) {
        regions.add(coloredRegion);
    }

    public int colorAtPoint(Point point) {
        for (ColoredRegion region : regions) {
            if (region.contains(point)) {
                return region.getColor();
            }
        }
        return DEFAULT_COLOR;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        for (ColoredRegion region : regions){
            g.setColor(region.getAwtColor());
            region.drawRegion(g);
        }
    }

    public static SurfaceMap getDefaultSurfaceMap(){



        SurfaceMap map = new SurfaceMap(BOUNDING_RECTANGLE);
        map.addRegion(new HorizontalLine(Color.GREEN, 10, 20, 5, 2));

        return map;
    }
}