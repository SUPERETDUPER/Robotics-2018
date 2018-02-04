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
import Common.utils.Logger;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

import java.io.IOException;

public final class Brick {
    private static final String LOG_TAG = Brick.class.getSimpleName();

    public static void waitForUserConfirmation() {
        if (!Config.useSimulator) {
            LCD.drawString("Press enter button to continue", 0, 0);
            Button.ENTER.waitForPress();
        } else {
            try {
                System.out.println("Press enter to continue");
                //noinspection ResultOfMethodCallIgnored
                System.in.read();
            } catch (IOException e) {
                Logger.error(LOG_TAG, e.toString());
            }
        }
    }
}