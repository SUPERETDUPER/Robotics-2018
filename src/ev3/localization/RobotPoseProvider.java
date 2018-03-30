/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.Config;
import common.TransmittableType;
import common.logger.Logger;
import common.particles.MCLData;
import ev3.communication.ComManager;
import ev3.navigation.Readings;
import ev3.robot.ColorSensors;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import lejos.utility.Delay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Singleton pattern
 * Based on odometry pose provider with the extra capability of storing a particle set and using it to refine it's location
 */
public class RobotPoseProvider implements MoveListener, PoseProvider {
    private static final String LOG_TAG = RobotPoseProvider.class.getSimpleName();

    private static final int NUM_PARTICLES = 300;

    @NotNull
    private final MoveProvider mp;
    @NotNull
    private MCLData data;

    @NotNull
    private final ArrayList<MCLDataListener> listeners = new ArrayList<>();

    /**
     * The amount the data has been shifted since the start of this move.
     * completedMove is null when the move starts and each time the data is updated (with update()) the completedMove is updated
     */
    @Nullable
    private Move completedMove;

    public RobotPoseProvider(@NotNull MoveProvider moveProvider, @NotNull Pose currentPose) {
        this.mp = moveProvider;
        this.data = new MCLData(Util.getNewParticleSet(currentPose, NUM_PARTICLES), currentPose);

        completedMove = getCurrentCompletedMove();

        notifyUpdate();
    }

    public synchronized void addListener(MCLDataListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(MCLDataListener listener) {
        for (int i = 0; i < listeners.size(); i++) {
            if (listener == listeners.get(i)) {
                listeners.remove(i);
            }
        }
    }

    public void startUpdater(ColorSensors colorSensors) {
        new Updater(colorSensors).start();
    }

    @NotNull
    public RobotPoseProvider get() {
        return null;
    }

    /**
     * Doesn't update the data object since we don't want to need to update the particles each time
     *
     * @return the current pose
     */
    @NotNull
    @Override
    public synchronized Pose getPose() {
        Move missingMove = Util.subtractMove(getCurrentCompletedMove(), completedMove);

        return Util.movePose(data.getCurrentPose(), missingMove);
    }

    @Override
    public synchronized void setPose(@NotNull Pose pose) {
        data = new MCLData(Util.getNewParticleSet(pose, NUM_PARTICLES), pose);
        completedMove = getCurrentCompletedMove();

        notifyUpdate();
    }

    @Override
    public synchronized void moveStarted(@NotNull Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Started move " + move.toString());
    }

    /**
     * Moves the particles and pose over by the amount remaining
     *
     * @param move         the move that was completed
     * @param moveProvider the move provider
     */
    @Override
    public synchronized void moveStopped(@NotNull Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Stopped move " + move.toString());

        Move missingMove = Util.subtractMove(Util.deepCopyMove(move), completedMove);

        data.setCurrentPose(Util.movePose(data.getCurrentPose(), missingMove));
        data.setParticles(Util.moveParticleSet(data.getParticles(), missingMove));

        completedMove = null;

        notifyUpdate();
    }

    public synchronized void update(@NotNull Readings readings) {
        Move move = getCurrentCompletedMove();

        Move missingMove = Util.subtractMove(move, completedMove);

        data.setParticles(Util.update(data.getParticles(), missingMove, readings));
        data.setCurrentPose(Util.movePose(data.getCurrentPose(), missingMove));
        data.setCurrentPose(Util.refineCurrentPose(data.getParticles())); //Updates current pose

        completedMove = move;

        notifyUpdate();
    }

    private void notifyUpdate() {
        for (MCLDataListener listener : listeners) {
            listener.notifyNewMCLData(data);
        }
    }

    public void sendCurrentPoseToPC() {
        if (Config.currentMode == Config.Mode.DUAL || Config.currentMode == Config.Mode.SIM) {
            ComManager.getDataSender().sendTransmittable(TransmittableType.CURRENT_POSE, getPose());
        }
    }

    @NotNull
    private Move getCurrentCompletedMove() {
        return Util.deepCopyMove(mp.getMovement());
    }

    /**
     * Check method checks if the color under the robot has changed. If so it calls the pose provider update method
     */
    public final class Updater extends Thread {
        private final String LOG_TAG = Updater.class.getSimpleName();

        private final ColorSensors colorSensors;

        Updater(ColorSensors colorSensors) {
            super();

            this.colorSensors = colorSensors;

            this.setDaemon(true);
            this.setName(Updater.class.getSimpleName());
        }

        @Override
        public void run() {
            //noinspection InfiniteLoopStatement
            while (true) {
                RobotPoseProvider.this.update(new SurfaceReadings(colorSensors.getColorSurfaceLeft()));

                Delay.msDelay(100);
            }
        }
    }
}