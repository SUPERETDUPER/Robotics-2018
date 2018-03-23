/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package pc;

import common.Logger;
import common.gui.EventTypes;
import common.gui.ParticleData;
import common.mapping.SurfaceMap;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pc.displayable.DisplayableParticleData;
import pc.displayable.DisplayablePath;

import java.io.DataInputStream;
import java.io.IOException;

public final class GUI extends Application implements DataChangeListener {
    private static final String LOG_TAG = GUI.class.getSimpleName();

    private static final Layer layerPath = new Layer(new DisplayablePath());
    @Nullable
    private static final Layer layerMCLData = new Layer(new DisplayableParticleData(null, null));

    @Nullable
    private static final Layer[] layers = {
            new Layer(new SurfaceMap()),
            layerMCLData,
            layerPath
    };

    /**
     * Called every new frame to redraw necessary layers
     */
    @NotNull
    private static final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            for (Layer layer : layers) {
                if (layer.flaggedToDraw()) {
                    layer.draw();
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
                ((DisplayableParticleData) layerMCLData.getDisplayable()).loadObject(dis);
                layerMCLData.flagToDraw();
                ((DisplayablePath) layerPath.getDisplayable()).setCurrentPose(((ParticleData) layerMCLData.getDisplayable()).getCurrentPose());
                break;
            case PATH:
                ((DisplayablePath) layerPath.getDisplayable()).loadObject(dis);
                layerPath.flagToDraw();
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

}