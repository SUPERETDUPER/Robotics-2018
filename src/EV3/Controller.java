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

package EV3;

import Common.utils.Logger;
import EV3.hardware.ChassisBuilder;
import EV3.navigation.CustomMCLPoseProvider;
import EV3.navigation.LineChecker;
import EV3.navigation.MyMovePilot;
import EV3.navigation.Readings;
import lejos.robotics.navigation.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Controller implements MoveListener, NavigationListener {

    private static final String LOG_TAG = Controller.class.getSimpleName();
    private static final double ANGULAR_ACCELERATION = 120;
    private static final double LINEAR_ACCELERATION = 400;
    private static final Pose STARTING_POSE = new Pose(500, 100, 0);

    private static final Controller controller = new Controller();

    @NotNull
    private final CustomMCLPoseProvider poseProvider;
    @NotNull
    private final Navigator navigator;

    private Controller() {
        MyMovePilot pilot = new MyMovePilot(ChassisBuilder.getChassis());

        pilot.setAngularAcceleration(ANGULAR_ACCELERATION);
        pilot.setLinearAcceleration(LINEAR_ACCELERATION);
        pilot.setLinearSpeed(pilot.getMaxLinearSpeed() * 0.8D);
        pilot.setAngularSpeed(pilot.getMaxAngularSpeed() * 0.8D);
        pilot.addMoveListener(this);

        poseProvider = new CustomMCLPoseProvider(pilot, STARTING_POSE);

        navigator = new Navigator(pilot, poseProvider);
        navigator.addNavigationListener(this);
        navigator.singleStep(true);
    }

    @NotNull
    @Contract(pure = true)
    public static Controller get() {
        return controller;
    }

    @Override
    public void moveStarted(Move move, MoveProvider moveProvider) {
        DataSender.sendPath(navigator.getPath());
    }

    @Override
    public void moveStopped(Move move, MoveProvider moveProvider) {
    }

    @Override
    public void pathComplete(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "Path complete");
    }

    @Override
    public void pathInterrupted(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "pathInterrupted");
    }

    @Override
    public void atWaypoint(Waypoint waypoint, Pose pose, int i) {
        Logger.info(LOG_TAG, "At Waypoint");
    }

    void goTo() {
        navigator.addWaypoint(new Waypoint(600, 200));
        navigator.addWaypoint(new Waypoint(1200, 400));
        navigator.addWaypoint(new Waypoint(300, 1000));
        navigator.followPath();
        LineChecker lineChecker = new LineChecker();
        while (navigator.isMoving()) {
            lineChecker.check();
            Thread.yield();
        }
        Logger.info(LOG_TAG, poseProvider.getPose().toString());
    }

    @NotNull
    public Pose getPose() {
        return poseProvider.getPose();
    }

    public void update(@NotNull Readings readings) {
        poseProvider.update(readings);
    }
}