/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc.gui;

import common.Config;
import common.TransmittableType;
import common.mapping.SurfaceMap;
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
import pc.communication.DataReceivedListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class GUI extends Application {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private static final List<Layer> staticLayers = new ArrayList<>();

    private static final Map<TransmittableType, UpdatableLayer> updatableLayers = new EnumMap<>(TransmittableType.class);

    @Nullable
    private static EventHandler<WindowEvent> onWindowCloseListener;

    private static boolean isGUIReady = false;

    public static final DataReceivedListener listener = new DataReceivedListener() {
        @Override
        public synchronized void dataReceived(@NotNull TransmittableType event, @NotNull DataInputStream dis) throws IOException {
            while (!isGUIReady) Thread.yield(); //So that we don't try to update a layer that has not yet been created

            updatableLayers.get(event).update(dis);

            if (event == TransmittableType.CURRENT_POSE) {
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

    public GUI() {
        super();

        SurfaceMap surfaceMap = new SurfaceMap(Config.PC_IMAGE_PATH);

        staticLayers.add(new SurfaceMapLayer(surfaceMap));

        updatableLayers.put(TransmittableType.PATH, new PathLayer(surfaceMap.getWidth(), surfaceMap.getHeight()));
        updatableLayers.put(TransmittableType.CURRENT_POSE, new CurrentPoseLayer(surfaceMap.getWidth(), surfaceMap.getHeight()));
        updatableLayers.put(TransmittableType.MCL_DATA, new ParticleDataLayer(surfaceMap.getWidth(), surfaceMap.getHeight()));

        isGUIReady = true;
    }


    @Override
    public void start(@NotNull Stage primaryStage) {
        Pane root = new Pane();

        for (Layer staticLayer : staticLayers) {
            root.getChildren().add(staticLayer);
            staticLayer.draw();
        }

        for (UpdatableLayer updatableLayer : updatableLayers.values()) {
            root.getChildren().add(updatableLayer);
        }

        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(onWindowCloseListener);

        primaryStage.show();

        animationTimer.start();
    }

    public static void launchGUI(@Nullable EventHandler<WindowEvent> onWindowCloseListener) {
        GUI.onWindowCloseListener = onWindowCloseListener;

        new Thread() {
            @Override
            public void run() {
                super.run();
                Application.launch(GUI.class, (String[]) null);
            }
        }.start();
    }
}