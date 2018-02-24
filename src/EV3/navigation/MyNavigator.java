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


import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.Path;

import java.util.ArrayList;

class MyNavigator implements WaypointListener {
    private Path _path;
    private boolean _keepGoing;
    private boolean _singleStep;
    private boolean _interrupted;
    private final MoveController _pilot;
    private PoseProvider poseProvider;
    private Pose _pose;
    private Waypoint _destination;
    private final double _radius;
    private int _sequenceNr;
    private final ArrayList<NavigationListener> _listeners;

    public MyNavigator(MoveController pilot) {
        this(pilot, null);
    }

    public MyNavigator(MoveController pilot, PoseProvider poseProvider) {
        this._path = new Path();
        this._keepGoing = false;
        this._singleStep = false;
        this._interrupted = false;
        this._pose = new Pose();
        this._listeners = new ArrayList<>();
        this._pilot = pilot;
        if (poseProvider == null) {
            this.poseProvider = new OdometryPoseProvider(this._pilot);
        } else {
            this.poseProvider = poseProvider;
        }

        this._radius = this._pilot instanceof ArcMoveController ? ((ArcMoveController) this._pilot).getMinRadius() : 0.0D;
        Nav _nav = new Nav();
        _nav.setDaemon(true);
        _nav.start();
    }

    public void setPoseProvider(PoseProvider aProvider) {
        this.poseProvider = aProvider;
    }

    public void addNavigationListener(NavigationListener listener) {
        this._listeners.add(listener);
    }

    public PoseProvider getPoseProvider() {
        return this.poseProvider;
    }

    public MoveController getMoveController() {
        return this._pilot;
    }

    public void setPath(Path path) {
        if (this._keepGoing) {
            this.stop();
        }

        this._path = path;
        this._singleStep = false;
        this._sequenceNr = 0;
    }

    public void clearPath() {
        if (this._keepGoing) {
            this.stop();
        }

        this._path.clear();
    }

    public Path getPath() {
        return this._path;
    }

    public void followPath(Path path) {
        this._path = path;
        this.followPath();
    }

    public void followPath() {
        if (!this._path.isEmpty()) {
            this._interrupted = false;
            this._keepGoing = true;
        }
    }

    public void singleStep(boolean yes) {
        this._singleStep = yes;
    }

    public void goTo(Waypoint destination) {
        this.addWaypoint(destination);
        this.followPath();
    }

    public void goTo(float x, float y) {
        this.goTo(new Waypoint((double) x, (double) y));
    }

    public void goTo(float x, float y, float heading) {
        this.goTo(new Waypoint((double) x, (double) y, (double) heading));
    }

    public boolean rotateTo(double angle) {
        float head = this.getPoseProvider().getPose().getHeading();

        double diff = angle - (double) head;

        while (diff > 180.0D) diff -= 360.0D;

        while (diff < -180.0D) {
            diff += 360.0D;
        }

        if (this.isMoving()) {
            return false;
        } else {
            if (this._pilot instanceof RotateMoveController) {
                ((RotateMoveController) this._pilot).rotate(diff, false);
            }

            return true;
        }
    }

    public void addWaypoint(Waypoint aWaypoint) {
        if (this._path.isEmpty()) {
            this._sequenceNr = 0;
            this._singleStep = false;
        }

        this._path.add(aWaypoint);
    }

    public void addWaypoint(float x, float y) {
        this.addWaypoint(new Waypoint((double) x, (double) y));
    }

    public void addWaypoint(float x, float y, float heading) {
        this.addWaypoint(new Waypoint((double) x, (double) y, (double) heading));
    }

    public void stop() {
        this._keepGoing = false;
        this._pilot.stop();
        this._interrupted = true;
        this.callListeners();
    }

    public Waypoint getWaypoint() {
        return this._path.size() <= 0 ? null : this._path.get(0);
    }

    public boolean pathCompleted() {
        return this._path.size() == 0;
    }

    public boolean waitForStop() {
        while (this._keepGoing) {
            Thread.yield();
        }

        return this._path.isEmpty();
    }

    public boolean isMoving() {
        return this._keepGoing;
    }

    public void pathGenerated() {
    }

    private void callListeners() {
        if (this._listeners != null) {
            this._pose = this.poseProvider.getPose();

            for (NavigationListener l : this._listeners) {
                if (this._interrupted) {
                    l.pathInterrupted(this._destination, this._pose, this._sequenceNr);
                } else {
                    l.atWaypoint(this._destination, this._pose, this._sequenceNr);
                    if (this._path.isEmpty()) {
                        l.pathComplete(this._destination, this._pose, this._sequenceNr);
                    }
                }
            }
        }

    }

    private class Nav extends Thread {

        private Nav() {
        }

        public void run() {
            for (; true; Thread.yield()) {
                for (; MyNavigator.this._keepGoing && MyNavigator.this._path != null && !MyNavigator.this._path.isEmpty(); Thread.yield()) {
                    MyNavigator.this._destination = MyNavigator.this._path.get(0);
                    MyNavigator.this._pose = MyNavigator.this.poseProvider.getPose();
                    float destinationRelativeBearing = MyNavigator.this._pose.relativeBearing(MyNavigator.this._destination);
                    if (!MyNavigator.this._keepGoing) {
                        break;
                    }

                    if (MyNavigator.this._radius == 0.0D) {
                        ((RotateMoveController) MyNavigator.this._pilot).rotate((double) destinationRelativeBearing, true);

                        while (MyNavigator.this._pilot.isMoving() && MyNavigator.this._keepGoing) {
                            Thread.yield();
                        }

                        if (!MyNavigator.this._keepGoing) {
                            break;
                        }
                    } else {
                        double minRadius = MyNavigator.this._pilot instanceof ArcMoveController ? ((ArcMoveController) MyNavigator.this._pilot).getMinRadius() : 0.0D;
                        Move[] moves;
                        if (MyNavigator.this._destination.isHeadingRequired()) {
                            moves = ArcAlgorithms.getBestPath(MyNavigator.this.poseProvider.getPose(), (float) minRadius, MyNavigator.this._destination.getPose(), (float) minRadius);
                        } else {
                            moves = ArcAlgorithms.getBestPath(MyNavigator.this.poseProvider.getPose(), MyNavigator.this._destination, (float) minRadius);
                        }

                        for (Move move : moves) {
                            ((ArcMoveController) MyNavigator.this._pilot).travelArc((double) move.getArcRadius(), (double) move.getDistanceTraveled(), false);
                            if (!MyNavigator.this._keepGoing) {
                                break;
                            }
                        }

                        while (MyNavigator.this._pilot.isMoving() && MyNavigator.this._keepGoing) {
                            Thread.yield();
                        }
                    }

                    MyNavigator.this._pose = MyNavigator.this.poseProvider.getPose();
                    if (!MyNavigator.this._keepGoing) {
                        break;
                    }

                    if (MyNavigator.this._radius == 0.0D) {
                        float distance = MyNavigator.this._pose.distanceTo(MyNavigator.this._destination);
                        MyNavigator.this._pilot.travel((double) distance, true);

                        while (MyNavigator.this._pilot.isMoving() && MyNavigator.this._keepGoing) {
                            Thread.yield();
                        }

                        MyNavigator.this._pose = MyNavigator.this.poseProvider.getPose();
                        if (!MyNavigator.this._keepGoing) {
                            break;
                        }

                        if (MyNavigator.this._destination.isHeadingRequired()) {
                            MyNavigator.this._pose = MyNavigator.this.poseProvider.getPose();
                            MyNavigator.this._destination.getHeading();
                            ((RotateMoveController) MyNavigator.this._pilot).rotate(MyNavigator.this._destination.getHeading() - (double) MyNavigator.this._pose.getHeading(), false);
                        }
                    }

                    if (MyNavigator.this._keepGoing && !MyNavigator.this._path.isEmpty()) {
                        if (!MyNavigator.this._interrupted) {
                            MyNavigator.this._path.remove(0);
                            MyNavigator.this._sequenceNr++;
                        }

                        MyNavigator.this.callListeners();
                        MyNavigator.this._keepGoing = !MyNavigator.this._path.isEmpty();
                        if (MyNavigator.this._singleStep) {
                            MyNavigator.this._keepGoing = false;
                        }
                    }
                }
            }

        }
    }
}

