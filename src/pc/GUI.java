/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.EventTypes;
import common.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import pc.displayable.*;

import java.io.DataInputStream;
import java.io.IOException;

public final class GUI extends Application {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private static final PathLayer layerPath = new PathLayer();
    private static final ParticleDataLayer layerMCLData = new ParticleDataLayer();
    private static final CurrentPoseLayer layerCurrentPose = new CurrentPoseLayer();

    private static final Layer[] layers = {
            new SurfaceMapLayer(),
            layerMCLData,
            layerPath,
            layerCurrentPose
    };

    static final DataChangeListener listener = new DataChangeListener() {
        /**
         * Called when the data has changed
         *
         * @param event the type of new data
         * @param dis   the data input stream to read from
         * @throws IOException thrown when reading from dataInputStream
         */
        @Override
        public synchronized void dataChanged(@NotNull EventTypes event, @NotNull DataInputStream dis) throws IOException {
            switch (event) {
                case MCL_DATA:
                    layerMCLData.updateLayer(dis);
                    layerMCLData.markNew();
                    layerPath.setCurrentPose(layerMCLData.getCurrentPose());
                    break;
                case PATH:
                    layerPath.updateLayer(dis);
                    layerPath.markNew();
                    break;
                case CURRENT_POSE:
                    layerCurrentPose.updateLayer(dis);
                    layerCurrentPose.markNew();
                    break;
                default:
                    Logger.error(LOG_TAG, "Not a recognized event type");
            }
        }

        /**
         * Shuts down the window if connection is lost
         */
        @Override
        public void connectionLost() {
//        Platform.exit();
        }
    };


    /**
     * Called every new frame to redraw necessary layers
     */
    @NotNull
    private static final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            for (Layer layer : layers) {
                if (layer instanceof UpdatableLayer) {
                    if (((UpdatableLayer) layer).hasNewData()) {
                        layer.draw();
                    }
                }
            }
        }
    };

    @Override
    public void start(@NotNull Stage primaryStage) {
        Pane root = new Pane(layers);

        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Connection.close();
            }
        });

        for (Layer layer : layers) {
            layer.draw();
        }

        primaryStage.show();

        animationTimer.start();
    }

    static void launchGUI() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Application.launch(GUI.class, null);
            }
        }.start();
    }
}