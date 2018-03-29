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
import pc.communication.DataReceivedListener;
import pc.communication.DataReceiver;

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

    static {

    }


    public GUI() {
        super();

        staticLayers.add(new SurfaceMapLayer());

        updatableLayers.put(TransmittableType.PATH, new PathLayer());
        updatableLayers.put(TransmittableType.CURRENT_POSE, new CurrentPoseLayer());
        updatableLayers.put(TransmittableType.MCL_DATA, new ParticleDataLayer());
    }

    public static final DataReceivedListener listener = new DataReceivedListener() {
        /**
         * Called when the data has changed
         *
         * @param event the type of new data
         * @param dis   the data input stream to read from
         * @throws IOException thrown when reading from dataInputStream
         */
        @Override
        public synchronized void dataReceived(@NotNull TransmittableType event, @NotNull DataInputStream dis) throws IOException {
            updatableLayers.get(event).update(dis);

            if (event == TransmittableType.MCL_DATA) {
                Pose currentPose = ((ParticleDataLayer) updatableLayers.get(TransmittableType.MCL_DATA)).getCurrentPose();
                ((PathLayer) updatableLayers.get(TransmittableType.PATH)).setCurrentPose(currentPose);
            }
        }
    };


    /**
     * Called every new frame to redraw necessary gui
     */
    @NotNull
    private static final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            for (UpdatableLayer layer : updatableLayers.values()) {
                layer.draw();
            }
        }
    };

    @Override
    public void start(@NotNull Stage primaryStage) {
        Pane root = new Pane();

        for (Layer staticLayer : staticLayers) {
            root.getChildren().add(staticLayer);
        }

        for (UpdatableLayer updatableLayer : updatableLayers.values()) {
            root.getChildren().add(updatableLayer);
        }

        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                DataReceiver.close();
            }
        });


        primaryStage.show();

        animationTimer.start();
    }

    public static void launchGUI() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Application.launch(GUI.class, (String[]) null);
            }
        }.start();
    }
}