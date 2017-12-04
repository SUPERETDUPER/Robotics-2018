package geometry;

import lejos.robotics.Color;
import lejos.robotics.geometry.Point;
import lejos.robotics.geometry.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SurfaceMap extends JComponent {

    private ArrayList<ColoredRegion> regions = new ArrayList<>();

    private static SurfaceMap colorSurfaceMap;

    static {
        colorSurfaceMap = new SurfaceMap(Color.WHITE, new Rectangle(0, 0, 236.2F, 114.3F));
        colorSurfaceMap.addRegion(new HorizontalLine(Color.GREEN, 10, 20, 5, 2));
        colorSurfaceMap.addRegion(new VerticalLine(Color.BLUE, 10, 20, 30, 2));
    }

    private final Rectangle boundingRectangle;
    private final int defaultColor;

    private SurfaceMap(int defaultColor, Rectangle boundingRectangle) {
        this.boundingRectangle = boundingRectangle;
        this.defaultColor = defaultColor;
    }

    private SurfaceMap(int defaultColor, Rectangle boundingRectangle, ArrayList<ColoredRegion> regions) {
        this(defaultColor, boundingRectangle);
        this.regions = regions;
    }

    public static SurfaceMap getSurfaceMap() {
        return colorSurfaceMap;
    }

    public boolean contains(Point point) {
        return boundingRectangle.contains(point);
    }

    private void addRegion(ColoredRegion coloredRegion) {
        regions.add(coloredRegion);
    }

    public int colorAtPoint(Point point) {
        for (ColoredRegion region : regions) {
            if (region.contains(point)) {
                return region.getColor();
            }
        }
        return defaultColor;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(ColoredRegion.getAwtColor(defaultColor));
        g.fillRect((int) boundingRectangle.x, (int) boundingRectangle.y, (int) boundingRectangle.getMaxX(), (int) boundingRectangle.getMaxY());
        for (ColoredRegion region : regions){
            g.setColor(ColoredRegion.getAwtColor(region.getColor()));
            region.drawRegion(g);
        }
    }
}