/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */


package ev3.navigation;

import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.NavigationListener;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.navigation.WaypointListener;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.ArcAlgorithms;
import lejos.robotics.pathfinding.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Taken from the lejos source code and modified to fix bugs
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MyNavigator implements WaypointListener {
    @NotNull
    private Path _path = new Path();

    /**
     * frequently tested by Nav.run() to break out of primary control loop
     * reset by stop(), and in Nav if _singleStep is set. or end of path is reached
     * set by followPath(xx) and goTo(xx)
     */
    private boolean _keepGoing = false;

    /**
     * if true, causes Nav.run to break whenever  waypoint is reached.
     */
    private boolean _singleStep = false;

    /**
     * set by Stop,  reset by followPath() , goTo()
     * used by  Nav.run(), callListeners
     */
    private boolean _interrupted = false;
    @NotNull
    private final MyMovePilot _pilot;
    @NotNull
    private PoseProvider poseProvider;
    private Waypoint _destination;
    private double _radius;
    private int _sequenceNr;
    @NotNull
    private ArrayList<NavigationListener> _listeners = new ArrayList<>();

    /**
     * Allocates a Navigator object,  using pilot that implements the ArcMoveController interface.
     */
    public MyNavigator(@NotNull MyMovePilot pilot) {
        this(pilot, null);
    }

    /**
     * Allocates a Navigator object using a pilot and a custom poseProvider, rather than the default
     * OdometryPoseProvider.
     *
     * @param pilot        the pilot
     * @param poseProvider the custom PoseProvider
     */
    public MyNavigator(@NotNull MyMovePilot pilot, @Nullable PoseProvider poseProvider) {
        _pilot = pilot;
        if (poseProvider == null)
            this.poseProvider = new OdometryPoseProvider(_pilot);
        else
            this.poseProvider = poseProvider;
        _radius = pilot.getMinRadius();
        Nav _nav = new Nav();
        _nav.setDaemon(true);
        _nav.start();
    }

    /**
     * Adds a NavigationListener that is informed when a the robot stops or
     * reaches a WayPoint.
     *
     * @param listener the NavitationListener
     */
    public void addNavigationListener(NavigationListener listener) {
        _listeners.add(listener);
    }

    /**
     * Returns the PoseProvider
     *
     * @return the PoseProvider
     */
    @NotNull
    public PoseProvider getPoseProvider() {
        return poseProvider;
    }

    /**
     * Returns the MoveController belonging to this object.
     *
     * @return the pilot
     */
    public MyMovePilot getMoveController() {
        return _pilot;
    }

    /**
     * Sets the path that the Navigator will traverse.
     * By default, the  robot will not stop along the way.
     * If the robot is moving when this method is called,  it stops and the current
     * path is replaced by the new one.
     *
     * @param path to be followed.
     */
    public void setPath(@NotNull Path path) {
        if (_keepGoing)
            stop();
        _path = path;
        _singleStep = false;
        _sequenceNr = 0;
    }

    /**
     * Clears the current path.
     * If the robot is moving when this method is called, it stops;
     */
    public void clearPath() {
        if (_keepGoing)
            stop();
        _path.clear();
    }

    /**
     * Gets the current path
     *
     * @return the path
     */
    @NotNull
    public Path getPath() {
        return _path;
    }

    /**
     * Starts the robot traversing the path. This method is non-blocking.
     *
     * @param path to be followed.
     */
    public void followPath(@NotNull Path path) {
        _path = path;
        followPath();
    }

    /**
     * Starts the robot traversing the current path.
     * This method is non-blocking;
     */
    public void followPath() {
        if (_path.isEmpty())
            return;
        _interrupted = false;
        _keepGoing = true;
//      RConsole.println("navigator followPath called");
    }

    /**
     * Controls whether the robot stops at each Waypoint; applies to the current path only.
     * The robot will move to the next Waypoint if you call {@link #followPath()}.
     *
     * @param yes if <code>true </code>, the robot stops at each Waypoint.
     */
    public void singleStep(boolean yes) {
        _singleStep = yes;
    }

    /**
     * Starts the robot moving toward the destination.
     * If no path exists, a new one is created consisting of the destination,
     * otherwise the destination is added to the path.  This method is non-blocking, and is
     * equivalent to <code>{@linkplain #addWaypoint(Waypoint) addWaypoint(destination);}
     * {@linkplain #followPath() followPath();}</code>
     *
     * @param destination the waypoint to be reached
     */
    public void goTo(Waypoint destination) {
        addWaypoint(destination);
        followPath();

    }

    /**
     * Starts the  moving toward the destination Waypoint created from
     * the parameters.
     * If no path exists, a new one is created,
     * otherwise the new Waypoint is added to the path.  This method is non-blocking, and is
     * equivalent to
     * <code>add(float x, float y);   followPath(); </code>
     *
     * @param x coordinate of the destination
     * @param y coordinate of the destination
     */
    public void goTo(float x, float y) {
        goTo(new Waypoint(x, y));
    }

    /**
     * Starts the  moving toward the destination Waypoint created from
     * the parameters.
     * If no path exists, a new one is created,
     * otherwise the new Waypoint is added to the path.  This method is non-blocking, and is
     * equivalent to
     * <code>add(float x, float y);   followPath(); </code>
     *
     * @param x       coordinate of the destination
     * @param y       coordinate of th destination
     * @param heading desired robot heading at arrival
     */
    public void goTo(float x, float y, float heading) {
        goTo(new Waypoint(x, y, heading));
    }

    /**
     * Rotates the robot to a new absolute heading. For example, rotateTo(0) will line the robot with the
     * x-axis, while rotateTo(90) lines it with the y-axis. If the robot is currently on the move to a
     * coordinate, this method will not attempt to rotate and it will return false.
     *
     * @param angle The absolute heading to rotate the robot to. Value is 0 to 360.
     * @return true if the rotation happened, false if the robot was moving while this method was called.
     */
    public boolean rotateTo(double angle) {
        float head = getPoseProvider().getPose().getHeading();
        double diff = angle - head;
        while (diff > 180) diff = diff - 360;
        while (diff < -180) diff = diff + 360;
        if (isMoving()) return false;
        _pilot.rotate(diff, false);
        return true;

    }

    /**
     * Adds a  Waypoint  to the end of the path.
     * Call {@link #followPath()} to start moving the along the current path.
     *
     * @param aWaypoint to be added
     */
    public void addWaypoint(Waypoint aWaypoint) {
        if (_path.isEmpty()) {
            _sequenceNr = 0;
            _singleStep = false;
        }
        _path.add(aWaypoint);
    }

    /**
     * Constructs an new Waypoint from the parameters and adds it to the end of the path.
     * Call {@link #followPath()} to start moving the along the current path.
     *
     * @param x coordinate of the waypoint
     * @param y coordinate of the waypoint
     */
    public void addWaypoint(float x, float y) {
        addWaypoint(new Waypoint(x, y));
    }

    /**
     * Constructs an new Waypoint from the parameters and adds it to the end of the path.
     * Call {@link #followPath()} to start moving the along the current path.
     *
     * @param x       coordinate of the waypoint
     * @param y       coordinate of the waypoint
     * @param heading the heading of the robot when it reaches the waypoint
     */
    public void addWaypoint(float x, float y, float heading) {
        addWaypoint(new Waypoint(x, y, heading));
    }

    /**
     * Stops the robot.
     * The robot will resume its path traversal if you call {@link #followPath()}.
     */
    public void stop() {
        _keepGoing = false;
        _pilot.stop();
        _interrupted = true;
        callListeners();
    }

    /**
     * Returns the waypoint to which the robot is presently moving.
     *
     * @return the waypoint ; null if the path is empty.
     */
    public Waypoint getWaypoint() {
        if (_path.size() <= 0)
            return null;
        return _path.get(0);
    }

    /**
     * Returns <code> true </code> if the the final waypoint has been reached
     *
     * @return <code> true </code>  if the path is completed
     */
    public boolean pathCompleted() {
        return _path.size() == 0;
    }

    /**
     * Waits for the robot  to stop for any reason ;
     * returns <code>true</code> if the robot stopped at the final Waypoint of
     * the  path.
     *
     * @return <code> true </code>  if the path is completed
     */
    public boolean waitForStop() {
        while (_keepGoing)
            Thread.yield();
        return _path.isEmpty();
    }

    /**
     * Returns <code>true<code> if the robot is moving toward a waypoint.
     *
     * @return <code>true </code> if moving.
     */
    public boolean isMoving() {
        return _keepGoing;
    }

    public void pathGenerated() {
        // Currently does nothing
    }

    private void callListeners() {
        for (NavigationListener l : _listeners)
            if (_interrupted)
                l.pathInterrupted(_destination,  poseProvider.getPose(), _sequenceNr);
            else {
                l.atWaypoint(_destination,  poseProvider.getPose(), _sequenceNr);
                if (_path.isEmpty())
                    l.pathComplete(_destination,  poseProvider.getPose(), _sequenceNr);
            }

    }

    public double normalizeRotationAmount(double amount) {
        while (Math.abs(amount - 360) < Math.abs(amount)) amount -= 360;
        while (Math.abs(amount + 360) < Math.abs(amount)) amount += 360;
        return amount;
    }

    /**
     * This inner class runs the thread that processes the waypoint queue
     */
    private class Nav extends Thread {
        @Override
        public void run() {
            //noinspection InfiniteLoopStatement
            for (;;Thread.yield()) {
                if (_keepGoing && !_path.isEmpty()) {
                    _destination = _path.get(0);

                    if (_radius == 0) {
                        //1. Rotate toward destination
                        float destinationRelativeBearing = poseProvider.getPose().relativeBearing(_destination);
                        _pilot.rotate(destinationRelativeBearing, true);

                        while (_pilot.isMoving() && _keepGoing) Thread.yield();
                        if (!_keepGoing) break;

                        //2. Travel to destination
                        float distance = poseProvider.getPose().distanceTo(_destination);
                        _pilot.travel(distance, true);

                        while (_pilot.isMoving() && _keepGoing) Thread.yield();
                        if (!_keepGoing) break;

                        //3. If required rotate toward final heading
                        if (_destination.isHeadingRequired()) {
                            _pilot.rotate(normalizeRotationAmount(_destination.getHeading()
                                    - poseProvider.getPose().getHeading()), false);

                            while (_pilot.isMoving() && _keepGoing) Thread.yield();
                            if (!_keepGoing) break;
                        }
                    } else {
                        // 1. Get shortest path:
                        Move[] moves;

                        if (_destination.isHeadingRequired()) {
                            moves = ArcAlgorithms.getBestPath(poseProvider.getPose(),
                                    (float) _pilot.getMinRadius(), _destination.getPose(), (float) _pilot.getMinRadius());
                        } else {
                            moves = ArcAlgorithms.getBestPath(poseProvider.getPose(),
                                    _destination, (float) _pilot.getMinRadius());
                        }

                        // 2. Drive the path
                        for (Move move : moves) {
                            _pilot.move(move, false);
                            if (!_keepGoing) break;
                        }

                        while (_pilot.isMoving() && _keepGoing) Thread.yield();
                        if (!_keepGoing) break;
                    }

                    if (_keepGoing && !_path.isEmpty()) {
                        if (!_interrupted) {
                            _path.remove(0);
                            _sequenceNr++;
                        }
                        callListeners();
                        _keepGoing = !_path.isEmpty();
                        if (_singleStep) _keepGoing = false;
                    }
                } // end while keepGoing
            }  // end forever loop
        }  // end run
    } // end Nav class
}