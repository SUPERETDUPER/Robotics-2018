/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3.localization;

import common.mapping.SurfaceMap;
import common.particles.MCLData;
import ev3.navigation.Offset;
import ev3.navigation.Readings;
import ev3.robot.Robot;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A pose provider that uses a particle algorithm
 * TODO Look over again for bugs
 */
public class RobotPoseProvider implements MoveListener, PoseProvider {
//    private static final String LOG_TAG = RobotPoseProvider.class.getSimpleName();

    private static final int NUM_PARTICLES = 50; //TODO Find optimal value

    @NotNull
    private final MoveProvider mp;
    private final SurfaceMap surfaceMap;
    @NotNull
    private ParticleSet data;

    @Nullable
    private RobotPoseProviderListener listener;

    /**
     * The amount the data has been shifted since the start of this moveData.
     * completedMove is null after the moveData has ended.
     * Each time the data is updated (with update()) the completedMove is updated
     */
    @Nullable
    private Move completedMove;

    public RobotPoseProvider(@NotNull SurfaceMap surfaceMap, @NotNull MoveProvider moveProvider, Pose startingPose) {
        this.surfaceMap = surfaceMap;
        this.mp = moveProvider;
        this.data = new ParticleSet(NUM_PARTICLES, surfaceMap.getBoundingRectangle(), startingPose);

        mp.addMoveListener(this);

        completedMove = mp.getMovement();

        notifyListener();
    }


    public void startUpdater(Robot.ColorSensors colorSensors) {
//        new Updater(colorSensors).start();
    }

    // LISTENER METHODS //
    // The listener is passed the particle and current pose data when it gets updated
    public void setListener(@Nullable RobotPoseProviderListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    private void notifyListener() {
        if (listener != null) {
            listener.notifyNewMCLData(data);
        }
    }

    /**
     * Doesn't update the data object since we don't want to need to update the particles each time getPose is called
     *
     * @return the current pose
     */
    @NotNull
    @Override
    @Contract(pure = true)
    public Pose getPose() {
        Move missingMove = Util.subtractMove(mp.getMovement(), completedMove);

        return Util.movePose(data.getCurrentPose(), missingMove);
    }

    @Override
    public synchronized void setPose(@NotNull Pose pose) {
        data = new ParticleSet(NUM_PARTICLES, surfaceMap.getBoundingRectangle(), pose);

        completedMove = mp.getMovement();

        notifyListener();
    }

    @Override
    public synchronized void moveStarted(@NotNull Move move, MoveProvider moveProvider) {
    }

    /**
     * Moves the particles and pose over by the amount remaining
     *
     * @param move         the moveData that was completed
     * @param moveProvider the moveData provider
     */
    @Override
    public synchronized void moveStopped(@NotNull Move move, MoveProvider moveProvider) {
        Move missingMove = Util.subtractMove(move, completedMove);

        data.moveData(missingMove);

        completedMove = null;

        notifyListener();
    }

    /**
     * Updates the particles and position using the algorithm
     */
    private synchronized void update(@NotNull Readings readings) {
        Move missingMove = Util.subtractMove(mp.getMovement(), completedMove);

        data.moveData(missingMove);
        data.update(readings);
//        data.setCurrentPose(Util.refineCurrentPose(data.getParticles())); //Updates current pose

        completedMove = missingMove;

        notifyListener(); //TODO Consider removing for optimization
    }

    /**
     * 1. Gets the readings for the left color sensor and apply to particles
     * 2. Gets the readings for the right color sensor and apply to particles
     * 3. Repeat forever
     * TODO Optimize so that it only runs when necessary
     */
    final class Updater extends Thread {
        private final Robot.ColorSensors colorSensors;

        Updater(Robot.ColorSensors colorSensors) {
            super();

            this.colorSensors = colorSensors;

            this.setDaemon(true);
            this.setName(Updater.class.getSimpleName());
        }

        @Override
        public void run() {
            //noinspection InfiniteLoopStatement
            for (;; Thread.yield()) {
                RobotPoseProvider.this.update(
                        new SurfaceReadings(surfaceMap, colorSensors.getColorSurfaceLeft(), Offset.LEFT_COLOR_SENSOR)
                );

                RobotPoseProvider.this.update(
                        new SurfaceReadings(surfaceMap, colorSensors.getColorSurfaceRight(), Offset.RIGHT_COLOR_SENSOR)
                );
            }
        }
    }

    public interface RobotPoseProviderListener {
        void notifyNewMCLData(MCLData data);
    }
}