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

package PC.GUI;

import Common.Config;
import Common.MCL.MCLData;
import Common.mapping.SurfaceMap;
import Common.utils.Logger;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GUI {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private static final JFrame window = new JFrame();

    private static final DisplayablePath path = new DisplayablePath();
    private static final MCLData mclData = new MCLData();

    private static final List<Displayable> contents = new ArrayList<>(Arrays.asList(
            SurfaceMap.get(),
            mclData,
            path
    ));

    private static final JComponent mainComponent = new JComponent() {
        @Override
        protected void paintComponent(Graphics g) {
            Logger.info(LOG_TAG, "Drawing GUI...");
            Graphics2D g2d = (Graphics2D) g;

            super.paintComponent(g2d);

            g2d.scale(Config.GUI_DISPLAY_RATIO, Config.GUI_DISPLAY_RATIO);

            for (Displayable layer : contents) {
                layer.displayOnGui(g2d);
            }
        }
    };

    static {
        window.getContentPane().add(mainComponent);
        window.setVisible(true);
        window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public static void updateMCLData(@NotNull DataInputStream dis) throws IOException {
        mclData.loadObject(dis);
        window.repaint();
    }

    public static void updatePaths(@NotNull DataInputStream dis) throws IOException {
        path.loadObject(dis);
        window.repaint();
    }

    public static Pose getCurrentPose() {
        return mclData.getCurrentPose();
    }

    public static void close() {
        window.dispose();
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {
    }
}