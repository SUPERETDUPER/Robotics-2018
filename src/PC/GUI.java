package PC;

import com.sun.istack.internal.NotNull;
import geometry.SurfaceMap;
import navigation.MyPoseProvider;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI extends JComponent {

    private static final GUI mapGui = new GUI();

    private final JFrame window;


    //List of layers to display on GUI
    private final ArrayList<Displayable> contents = new ArrayList<>(Arrays.asList(
            SurfaceMap.get(),
            MyPoseProvider.get(),
            MyPoseProvider.get().getParticleSet()
    ));

    /*
    Creates a window and loads it's content
     */
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

        for (Displayable content : contents) {
            content.displayOnGUI(g);
        }
    }

    public void close() {
        window.dispose();
    }
}