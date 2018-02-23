/*
 * MIT License
 *
 * Copyright (c) [2018] [Martin Staadecker]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package EV3.navigation;

import Common.Config;
import Common.Logger;
import Common.Particles.ParticleData;
import EV3.DataSender;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

/**
 * Odometry pose provider with the extra capability of storing a particle set and using it to refine it's location
 */
public class ParticlePoseProvider implements MoveListener, PoseProvider {
    private static final String LOG_TAG = ParticlePoseProvider.class.getSimpleName();

    @NotNull
    private final MoveProvider mp;
    private ParticleSet particleSet;

    private Pose currentPose;

    private Move completedMovePose;
    private Move competedMoveParticle;

    public ParticlePoseProvider(@NotNull MoveProvider moveProvider, @NotNull Pose startingPose) {
        this.mp = moveProvider;
        moveProvider.addMoveListener(this);

        this.currentPose = startingPose;
        this.particleSet = new ParticleSet(startingPose); //Create particle set

        Move currentMove = this.mp.getMovement();

        this.completedMovePose = currentMove;
        this.competedMoveParticle = currentMove;

        Logger.info(LOG_TAG, "Starting at " + startingPose.toString() + ". particles generated");

        updatePC();
    }

    @Override
    public synchronized Pose getPose() {
        return ParticleSetUtil.movePose(currentPose, ParticleSetUtil.subtractMove(mp.getMovement(), completedMovePose));
    }

    public synchronized void setPose(@NotNull Pose pose) {
        currentPose = pose;
        particleSet = new ParticleSet(pose);

        completedMovePose = mp.getMovement();
        competedMoveParticle = mp.getMovement();

        updatePC();
    }

    @Override
    public void moveStarted(@NotNull Move move, MoveProvider moveProvider) {
        Logger.debug(LOG_TAG, "Move started " + move.toString());
    }

    @Override
    public synchronized void moveStopped(@NotNull Move move, MoveProvider moveProvider) {
        Move missingMovePose = ParticleSetUtil.subtractMove(move, completedMovePose);
        Move missingMoveParticles = ParticleSetUtil.subtractMove(move, competedMoveParticle);

        currentPose = ParticleSetUtil.movePose(currentPose, missingMovePose);
        particleSet.moveParticles(missingMoveParticles);

        completedMovePose = null;
        competedMoveParticle = null;

        Logger.debug(LOG_TAG, "Move stopped " + move.toString());
        updatePC();
    }

    public void updatePC() {
        if (Config.usePC) {
            DataSender.sendParticleData(new ParticleData(particleSet.getParticles(), this.getPose()));
        }
    }

    public synchronized void update(@NotNull Readings readings) {
        Move move = mp.getMovement();

        particleSet.moveParticles(ParticleSetUtil.subtractMove(move, competedMoveParticle)); //Shift particles
        competedMoveParticle = move;

        particleSet.weightParticles(readings); //Recalculate all the particle weights
        particleSet.resample();//Re samples for highest weights

        currentPose = particleSet.estimateCurrentPose(); //Updates current pose
        completedMovePose = move;

        updatePC(); //SendToPc

        Logger.info(LOG_TAG, "Updated with readings. New position is " + this.getPose().toString());
    }
}