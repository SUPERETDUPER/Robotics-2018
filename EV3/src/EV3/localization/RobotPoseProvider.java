/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package EV3.localization;

import Common.Config;
import Common.Logger;
import EV3.DataSender;
import EV3.navigation.Readings;
import Common.GUI.ParticleData;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

/**
 * Based on odometry pose provider with the extra capability of storing a particle set and using it to refine it's location
 */
public class RobotPoseProvider implements MoveListener, PoseProvider {
    private static final String LOG_TAG = RobotPoseProvider.class.getSimpleName();

    private static final RobotPoseProvider mParticlePoseProvider = new RobotPoseProvider();

    private MoveProvider mp;
    private ParticleSet particleSet;

    private Pose currentPose;

    private Move completedMove;

    private RobotPoseProvider() {
    }

    public static RobotPoseProvider get() {
        return mParticlePoseProvider;
    }

    public void addMoveProvider(MoveProvider moveProvider) {
        this.mp = moveProvider;
        moveProvider.addMoveListener(this);
    }

    @Override
    public synchronized Pose getPose() {
        Move missingMove = Util.subtractMove(mp.getMovement(), completedMove);

        return Util.movePose(currentPose, missingMove);
    }

    public synchronized void setPose(@NotNull Pose pose) {
        currentPose = pose;
        particleSet = new ParticleSet(pose);
        completedMove = deepCopyMove(mp.getMovement());

        updatePC();
    }

    @Override
    public void moveStarted(@NotNull Move move, MoveProvider moveProvider) {
        Logger.debug(LOG_TAG, "Move started : " + move.toString());
    }

    @Override
    public synchronized void moveStopped(@NotNull Move move, MoveProvider moveProvider) {
        Logger.info(LOG_TAG, "Move stopped  : " + move.toString());

        Move missingMove = Util.subtractMove(move, completedMove);

        currentPose = Util.movePose(currentPose, missingMove);
        particleSet.moveParticles(missingMove);

        completedMove = null;

        updatePC();
    }

    public void updatePC() {
        if (Config.usePC) {
            DataSender.sendParticleData(new ParticleData(particleSet.getParticles(), this.getPose()));
        }
    }

    public synchronized void update(@NotNull Readings readings) {
        Move move = mp.getMovement();

        particleSet.moveParticles(Util.subtractMove(move, completedMove)); //Shift particles
        currentPose = Util.movePose(currentPose, Util.subtractMove(move, completedMove));

        completedMove = deepCopyMove(move);

        particleSet.weightParticles(readings); //Recalculate all the particle weights
        particleSet.resample();//Re samples for highest weights
//        currentPose = particleSet.estimateCurrentPose(); //Updates current pose

        updatePC(); //SendToPc

        Logger.info(LOG_TAG, "Updated with readings. New position is " + this.getPose().toString());
    }

    private static Move deepCopyMove(Move move) {
        return new Move(move.getMoveType(), move.getDistanceTraveled(), move.getAngleTurned(), move.getTravelSpeed(), move.getRotateSpeed(), move.isMoving());
    }
}