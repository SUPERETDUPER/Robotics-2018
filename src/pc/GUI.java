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

public final class GUI extends Application implements DataChangeListener {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private static final DisplayablePath layerPath = new DisplayablePath();
    private static final DisplayableParticleData layerMCLData = new DisplayableParticleData();
    private static final DisplayablePose layerCurrentPose = new DisplayablePose();

    private static final Layer[] layers = {
            new DisplayableSurfaceMap(),
            layerMCLData,
            layerPath,
            layerCurrentPose
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
                    UpdatableLayer updatableLayer = (UpdatableLayer) layer;

                    if (updatableLayer.hasNewData()) {
                        updatableLayer.draw();
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

        Connection.setListener(this);

        primaryStage.show();

        animationTimer.start();
    }

    /**
     * Called when the data has changed
     *
     * @param event the type of new data
     * @param dis   the data input stream to read from
     * @throws IOException thrown when reading from dataInputStream
     */
    @Override
    public synchronized void dataChanged(@NotNull EventTypes event, @NotNull DataInputStream dis) throws IOException {
        if (event == EventTypes.LOG) {
            System.out.println(dis.readUTF());
            return;
        }

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

}