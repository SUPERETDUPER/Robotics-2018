package PC;

import geometry.SurfaceMap;
import navigation.CustomMCLPoseProvider;
import utils.Config;

import javax.swing.*;
import java.awt.*;

public class MapGUI extends JComponent {
    private static final MapGUI mapGui = new MapGUI();

    private JFrame window;

    private MapGUI() {
        window = new JFrame();
        window.getContentPane().add(this);
        window.setVisible(true);
        window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public static MapGUI get() {
        return mapGui;
    }

    public static int adjustSize(float original) {
        return (int) (original * Config.GUI_DISPLAY_RATIO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        SurfaceMap.paintComponent(g);
        CustomMCLPoseProvider.get().paintComponent(g);
    }

    public static void init() {
    }

    public void close() {
        window.dispose();
    }
}