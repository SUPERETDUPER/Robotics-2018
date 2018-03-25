/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.EventTypes;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;
import pc.layers.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public final class GUI extends Application {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private static final Layer[] staticLayers = {
            new SurfaceMapLayer(),
    };

    private static final Map<EventTypes, UpdatableLayer> updatableLayers = new EnumMap<>(EventTypes.class);

    static {
        updatableLayers.put(EventTypes.PATH, new PathLayer());
        updatableLayers.put(EventTypes.CURRENT_POSE, new CurrentPoseLayer());
        updatableLayers.put(EventTypes.MCL_DATA, new ParticleDataLayer());
    }

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
            updatableLayers.get(event).update(dis);

            if (event == EventTypes.MCL_DATA) {
                Pose currentPose = ((ParticleDataLayer) updatableLayers.get(EventTypes.MCL_DATA)).getCurrentPose();
                ((PathLayer) updatableLayers.get(EventTypes.PATH)).setCurrentPose(currentPose);
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
            for (UpdatableLayer layer : updatableLayers.values()) {
                layer.draw();
            }
        }
    };

    @Override
    public void start(@NotNull Stage primaryStage) {
        Pane root = new Pane(staticLayers);

        for (UpdatableLayer updatableLayer : updatableLayers.values()) {
            root.getChildren().add(updatableLayer);
        }

        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                DataReader.close();
            }
        });


        primaryStage.show();

        animationTimer.start();
    }

    static void launchGUI() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Application.launch(GUI.class, (String[]) null);
            }
        }.start();
    }
}