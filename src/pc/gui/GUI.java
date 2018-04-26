/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import common.TransmittableType;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pc.DataReceiver;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Most of this class is static since there is no way to access the instance
 */
public final class GUI extends Application {
    // --Commented out by Inspection (25/04/18 8:37 PM):private static final String LOG_TAG = GUI.class.getSimpleName();

    //Layers that are updated by the DataReceivedListener
    @NotNull
    private static final Map<TransmittableType, UpdatableLayer> updatableLayers = new EnumMap<>(TransmittableType.class);

    @Nullable
    private static EventHandler<WindowEvent> onWindowCloseListener;

    private static boolean isLoaded = false;

    @NotNull
    public static final DataReceiver.DataReceivedListener listener = new DataReceiver.DataReceivedListener() {
        @Override
        public synchronized void dataReceived(@NotNull TransmittableType eventType, @NotNull DataInputStream dataInputStream) throws IOException {
            while (!isLoaded) Thread.yield(); //So that we don't try to update a layer that has not yet been created

            updatableLayers.get(eventType).update(dataInputStream);

            //Special case to communicate current pose with path
            if (eventType == TransmittableType.CURRENT_POSE) {
                Pose currentPose = ((CurrentPoseLayer) updatableLayers.get(TransmittableType.CURRENT_POSE)).getPose();

                ((PathLayer) updatableLayers.get(TransmittableType.PATH)).setCurrentPose(currentPose);
            }
        }
    };

    /**
     * Called every new frame to redraw necessary gui
     */
    @NotNull
    private final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            for (UpdatableLayer layer : updatableLayers.values()) {
                layer.draw();
            }
        }
    };

    public static void launchGUI(@Nullable EventHandler<WindowEvent> onWindowCloseListener) {
        GUI.onWindowCloseListener = onWindowCloseListener;

        //Starts the application
        new Thread() {
            @Override
            public void run() {
                Application.launch(GUI.class, (String[]) null);
            }
        }.start();
    }

    /**
     * Initializes GUI
     */
    @Override
    public void start(@NotNull Stage primaryStage) {
        primaryStage.setTitle("Robotics 2018");

        Pane root = new Pane();

        //Create the map layer
        SurfaceMapLayer surfaceMapLayer = new SurfaceMapLayer();
        surfaceMapLayer.draw();
        root.getChildren().add(surfaceMapLayer);

        //Create all the other updatable layers with the map layer width and height
        updatableLayers.put(TransmittableType.PATH, new PathLayer(surfaceMapLayer.getWidth(), surfaceMapLayer.getHeight()));
        updatableLayers.put(TransmittableType.CURRENT_POSE, new CurrentPoseLayer(surfaceMapLayer.getWidth(), surfaceMapLayer.getHeight()));
        updatableLayers.put(TransmittableType.MCL_DATA, new ParticleDataLayer(surfaceMapLayer.getWidth(), surfaceMapLayer.getHeight()));

        //Add the layers to the pane
        for (UpdatableLayer updatableLayer : updatableLayers.values()) {
            root.getChildren().add(updatableLayer);
        }

        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(onWindowCloseListener);

        primaryStage.show();

        animationTimer.start(); //Starts the animation timer to redraw the frames

        isLoaded = true;
    }
}