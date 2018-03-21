/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package PC.GUI;

import Common.Logger;
import Common.mapping.SurfaceMap;
import PC.Connection;
import PC.DataChangeListener;
import Common.GUI.EventTypes;
import Common.GUI.DisplayablePath;
import Common.GUI.ParticleData;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lejos.robotics.navigation.Pose;

import java.io.DataInputStream;
import java.io.IOException;

public final class GUI extends Application implements DataChangeListener {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private static final Layer layerPath = new Layer(new DisplayablePath());
    private static final Layer layerMCLData = new Layer(new ParticleData(null, null));

    private static final Layer[] layers = {
            new Layer(new SurfaceMap()),
            layerMCLData,
            layerPath
    };

    /**
     * Called every new frame to redraw necessary layers
     */
    private static AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            for (Layer layer : layers) {
                if (!layer.isDrawn()) {
                    layer.draw();
                }
            }
        }
    };

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane(layers);

        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Connection.close();
            }
        });

        Connection.setListener(this);

        primaryStage.show();

        animationTimer.start();
    }

    /**
     * Called when the data has changed
     *
     * @param event the type of new data
     * @param dis   the data input strem to read from
     * @throws IOException thrown when reading from dataInputStream
     */
    @Override
    public void dataChanged(EventTypes event, DataInputStream dis) throws IOException {
        if (event == EventTypes.LOG) {
            System.out.println(dis.readUTF());
            return;
        }

        switch (event) {
            case MCL_DATA:
                ((ParticleData) layerMCLData.getDisplayable()).loadObject(dis);
                layerMCLData.markNotDrawn();
                ((DisplayablePath) layerPath.getDisplayable()).setCurrentPose(((ParticleData) layerMCLData.getDisplayable()).getCurrentPose());
                layerPath.markNotDrawn();
                break;
            case PATH:
                ((DisplayablePath) layerPath.getDisplayable()).loadObject(dis);
                layerPath.markNotDrawn();
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
        Platform.exit();
    }

    public static Pose getCurrentPose() {
        return ((ParticleData) layerMCLData.getDisplayable()).getCurrentPose();
    }
}