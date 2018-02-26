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

import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;

/**
 * Responsible of offsetting readings
 */
public final class Offset {
    // (x,y) offsets when robot is facing to the right (heading = 0)
    private static final float[] RELATIVE_OFFSET_LEFT = {-1, -1};
    private static final float[] RELATIVE_OFFSET_RIGHT = {1, -1};

    public static Point leftColorSensor(Pose pose) {
        return offset(pose, RELATIVE_OFFSET_LEFT[0], RELATIVE_OFFSET_RIGHT[1]);
    }

    public static Point rightColorSensor(Pose pose) {
        return offset(pose, RELATIVE_OFFSET_RIGHT[0], RELATIVE_OFFSET_RIGHT[1]);
    }

    private static Point offset(Pose pose, float xOffset, float yOffset) {
        double originalTheta = Math.atan(yOffset / xOffset);
        double hypotenuse = Math.sqrt(xOffset * xOffset + yOffset * yOffset); //Pythagorean theorem

        double newTheta = originalTheta + Math.toRadians(pose.getHeading());

        float newXOffset = (float) (Math.cos(newTheta) * hypotenuse);
        float newYOffset = (float) (Math.sin(newTheta) * hypotenuse);

        return new Point(pose.getX() + newXOffset, pose.getY() + newYOffset);
    }
}
