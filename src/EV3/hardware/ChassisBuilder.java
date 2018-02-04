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

package EV3.hardware;

import Common.Config;
import EV3.sim.AbstractMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import org.jetbrains.annotations.NotNull;

public class ChassisBuilder {

    private static final double WHEEL_DIAMETER = 55.9;
    private static final double WHEEL_OFFSET = 82.4;

    @NotNull
    public static Chassis getChassis() {
        RegulatedMotor leftMotor;
        RegulatedMotor rightMotor;

        if (Config.useSimulator) {
            leftMotor = new AbstractMotor("Left motor");
            rightMotor = new AbstractMotor("Right motor");
        } else {
            leftMotor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_LEFT);
            rightMotor = new EV3LargeRegulatedMotor(Ports.PORT_MOTOR_RIGHT);
        }

        Wheel[] wheels = new Wheel[]{
                WheeledChassis.modelWheel(leftMotor, WHEEL_DIAMETER).offset(WHEEL_OFFSET),
                WheeledChassis.modelWheel(rightMotor, WHEEL_DIAMETER).offset(-WHEEL_OFFSET)
        };

        return new WheeledChassis(wheels, WheeledChassis.TYPE_DIFFERENTIAL);
    }
}
