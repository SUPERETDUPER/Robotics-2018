//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package EV3.navigation;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.Path;

import java.util.ArrayList;

public class MyNavigator implements WaypointListener {
    private Path _path = new Path();
    private boolean _keepGoing = false;
    private boolean _singleStep = false;
    private boolean _interrupted = false;
    private MoveController _pilot;
    private PoseProvider poseProvider;
    private Waypoint _destination;
    private int _sequenceNr;
    private ArrayList<NavigationListener> _listeners = new ArrayList<>();

    public MyNavigator(MoveController pilot, PoseProvider poseProvider) {
        this._pilot = pilot;
        this.poseProvider = poseProvider;

        MyNavigator.Nav navThread = new MyNavigator.Nav();
        navThread.setDaemon(true);
        navThread.start();
    }

    public void addNavigationListener(NavigationListener listener) {
        this._listeners.add(listener);
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
        float head = poseProvider.getPose().getHeading();

        double diff = angle - head;

        while (diff > 180) diff -= 360;
        while (diff < -180.0D) diff += 360.0D;

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
        Pose pose = this.poseProvider.getPose();
        for (NavigationListener nv : _listeners) {

            if (this._interrupted) {
                nv.pathInterrupted(this._destination, pose, this._sequenceNr);
            } else {
                nv.atWaypoint(this._destination, pose, this._sequenceNr);
                if (this._path.isEmpty()) {
                    nv.pathComplete(this._destination, pose, this._sequenceNr);
                }
            }


        }

    }

    private class Nav extends Thread {
        private Nav() {
        }

        public void run() {
            for (;; Thread.yield()) {
                for (; MyNavigator.this._keepGoing && MyNavigator.this._path != null && !MyNavigator.this._path.isEmpty(); Thread.yield()) {

                    MyNavigator.this._destination = MyNavigator.this._path.get(0);
                    Pose pose = MyNavigator.this.poseProvider.getPose();
                    float destinationRelativeBearing = pose.relativeBearing(MyNavigator.this._destination);


                    ((RotateMoveController) MyNavigator.this._pilot).rotate(destinationRelativeBearing, true);

                    while (MyNavigator.this._pilot.isMoving() && MyNavigator.this._keepGoing) {
                        Thread.yield();
                    }

                    if (!MyNavigator.this._keepGoing) {
                        break;
                    }


                    pose = MyNavigator.this.poseProvider.getPose();
                    float distance = pose.distanceTo(MyNavigator.this._destination);
                    MyNavigator.this._pilot.travel(distance, true);

                    while (MyNavigator.this._pilot.isMoving() && MyNavigator.this._keepGoing) {
                        Thread.yield();
                    }

                    if (!MyNavigator.this._keepGoing) {
                        break;
                    }

                    if (MyNavigator.this._destination.isHeadingRequired()) {
                        pose = MyNavigator.this.poseProvider.getPose();
                        ((RotateMoveController) MyNavigator.this._pilot).rotate(MyNavigator.this._destination.getHeading() - (double) pose.getHeading(), false);

                        while (MyNavigator.this._pilot.isMoving() && MyNavigator.this._keepGoing) {
                            Thread.yield();
                        }

                        if (!MyNavigator.this._keepGoing) {
                            break;
                        }
                    }


                    if (MyNavigator.this._keepGoing && !MyNavigator.this._path.isEmpty()) {
                        if (!MyNavigator.this._interrupted) { //TODO Replace with if at close location
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
