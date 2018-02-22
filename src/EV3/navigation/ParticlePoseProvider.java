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
import Common.Particles.ParticleData;
import Common.Logger;
import EV3.DataSender;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import org.jetbrains.annotations.NotNull;

/**
 * Odometry pose provider with the extra capability of storing a particle set and using it to refine it's location
 */
public class ParticlePoseProvider extends OdometryPoseProvider {
    private static final String LOG_TAG = ParticlePoseProvider.class.getSimpleName();

    @NotNull
    private final MoveProvider mp;
    private ParticleSet particleSet;

    private float distanceParticlesTraveled;
    private float distanceParticlesRotated;

    public ParticlePoseProvider(@NotNull MoveProvider moveProvider, @NotNull Pose startingPose) {
        super(moveProvider);
        this.mp = moveProvider;
        this.setPose(startingPose);

        particleSet = new ParticleSet(startingPose); //Create particle set

        Logger.info(LOG_TAG, "Starting at " + startingPose.toString() + ". particles generated");

        updatePC();
    }

    public synchronized void setPose(@NotNull Pose pose) {
        super.setPose(pose);
        particleSet = new ParticleSet(pose);

        updatePC();
    }

    @Override
    public synchronized void moveStarted(@NotNull Move move, MoveProvider moveProvider) {
        super.moveStarted(move, moveProvider);

        distanceParticlesTraveled = 0;
        distanceParticlesRotated = 0;

        Logger.debug(LOG_TAG, "Move started " + move.toString());
    }

    @Override
    public synchronized void moveStopped(@NotNull Move move, MoveProvider moveProvider) {
        super.moveStopped(move, moveProvider);

        Logger.debug(LOG_TAG, "Move stopped " + move.toString());
        moveParticles(move);
        updatePC();
    }

    public void updatePC() {
        if (Config.usePC) {
            DataSender.sendParticleData(new ParticleData(particleSet.getParticles(), this.getPose()));
        }
    }

    public synchronized void update(@NotNull Readings readings) {
        moveParticles(mp.getMovement()); //Shift particles
        particleSet.weightParticles(readings); //Recalculate all the particle weights
        particleSet.resample();//Re samples for highest weights
        super.setPose(particleSet.estimateCurrentPose()); //Updates current pose
        updatePC(); //SendToPc

        Logger.info(LOG_TAG, "Updated with readings. New position is " + this.getPose().toString());
    }


    private synchronized void moveParticles(@NotNull Move move) {
        switch (move.getMoveType()) {
            case STOP:
                return;
            case TRAVEL:
                particleSet.shiftParticles(move.getDistanceTraveled() - distanceParticlesTraveled);
                distanceParticlesTraveled = move.getDistanceTraveled();
                break;
            case ROTATE:
                particleSet.rotateParticles(move.getAngleTurned() - distanceParticlesRotated);
                distanceParticlesRotated = move.getAngleTurned();
                break;
            default:
                Logger.warning(LOG_TAG, "Move type not implemented " + move.toString());
        }
    }
}