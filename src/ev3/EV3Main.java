/*
 * Copyright (c) [2018] [Jonathan McIntosh, Martin Staadecker, Ryan Zazo]
 */

package ev3;

import common.Config;
import ev3.communication.ComManager;
import ev3.robot.Robot;
import ev3.robot.hardware.EV3Robot;
import ev3.robot.sim.SimRobot;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

final class EV3Main {
    private static final String LOG_TAG = EV3Main.class.getSimpleName();

    private static Robot robot;

    public static void main(String[] args) {
        if (!initialize()) return;

        runMain();

        robot.getBrick().waitForUserConfirmation();

        cleanUp();
    }

    private static boolean initialize() {
        if (Config.currentMode == Config.Mode.DUAL || Config.currentMode == Config.Mode.SIM) {
            boolean success = ComManager.build();

            if (!success) {
                return false;
            }
        }

        if (Config.currentMode == Config.Mode.SIM) {
            robot = new SimRobot();
        } else {
            robot = new EV3Robot();
        }
        return true;
    }

    private static void runMain() {
       // Brain.start(robot);

        EV3LargeRegulatedMotor A = new EV3LargeRegulatedMotor(MotorPort.A);
        EV3LargeRegulatedMotor C = new EV3LargeRegulatedMotor(MotorPort.C);
        A.forward();
        C.forward();
        Delay.msDelay(2000);
        A.stop();
        C.stop();

        robot.getPaddle().useMotor(true);

        /*robot.getArm().goToFoodHanging(true);
        Delay.msDelay(2000);
        robot.getArm().goToFoodIn(true);
        Delay.msDelay(2000);
        robot.getArm().goToFoodHanging(true);
        Delay.msDelay(2000);
        robot.getArm().goToFoodOut(true);
*/

    }

    private static void cleanUp() {
        if (ComManager.running()) {
            ComManager.stop();
        }
    }
}