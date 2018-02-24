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

package Common;

import Common.Logger.LogTypes;

/**
Environment specific and run specific settings
 */
public final class Config {

    public static final boolean DISPLAY_PARTICLE_WEIGHT = false;
    public final static int SIM_SPEED_REDUCING_FACTOR = 3;

    public static boolean runningOnEV3;

    public static final boolean useSimulator = true;
    public static final boolean usePC = true;

    public static final float GUI_DISPLAY_RATIO = 0.8F;
    public static final String EV3_IP_ADDRESS = "10.0.1.1";
    public static final int PORT_TO_CONNECT_ON_EV3 = 8888;

    static final LogTypes IMPORTANCE_TO_PRINT = LogTypes.DEBUG;
}
