package PC;

import com.sun.istack.internal.NotNull;
import geometry.SurfaceMap;
import navigation.MyPoseProvider;
import utils.Config;

import javax.swing.*;
import java.awt.*;

public class GUI extends JComponent {

    private static final GUI mapGui = new GUI();

    private final JFrame window;

    private GUI() {
        window = new JFrame();
        window.getContentPane().add(this);
        window.setVisible(true);
        window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    @NotNull
    public static GUI get() {
        return mapGui;
    }

    @NotNull
    public static int adjustSize(@NotNull float original) {
        return (int) (original * Config.GUI_DISPLAY_RATIO);
    }

    public static void init() {
    }

    @Override
    protected void paintComponent(@NotNull Graphics g) {
        super.paintComponent(g);

        SurfaceMap.get().displayOnGUI(g);
        MyPoseProvider.get().displayOnGUI(g);
    }

    public void close() {
        window.dispose();
    }
}