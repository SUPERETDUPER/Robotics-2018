package PC;

import geometry.SurfaceMap;
import navigation.CustomMCLPoseProvider;

import javax.swing.*;
import java.awt.*;

public class MapGUI extends JComponent {
    private static final float DISPLAY_COEFFICIENT = 8;

    public static int adjustSize(float original) {
        return (int) (original * DISPLAY_COEFFICIENT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        SurfaceMap.paintComponent(g);
        CustomMCLPoseProvider.get().paintComponent(g);
    }
}