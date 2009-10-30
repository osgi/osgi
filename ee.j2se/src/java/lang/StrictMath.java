/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang;
public final class StrictMath {
	public final static double E = 2.718281828459045d;
	public final static double PI = 3.141592653589793d;
	public static native double IEEEremainder(double var0, double var1);
	public static double abs(double var0) { return 0.0d; }
	public static float abs(float var0) { return 0.0f; }
	public static int abs(int var0) { return 0; }
	public static long abs(long var0) { return 0l; }
	public static native double acos(double var0);
	public static native double asin(double var0);
	public static native double atan(double var0);
	public static native double atan2(double var0, double var1);
	public static native double cbrt(double var0);
	public static native double ceil(double var0);
	public static native double cos(double var0);
	public static native double cosh(double var0);
	public static native double exp(double var0);
	public static native double expm1(double var0);
	public static native double floor(double var0);
	public static native double hypot(double var0, double var1);
	public static native double log(double var0);
	public static native double log10(double var0);
	public static native double log1p(double var0);
	public static double max(double var0, double var1) { return 0.0d; }
	public static float max(float var0, float var1) { return 0.0f; }
	public static int max(int var0, int var1) { return 0; }
	public static long max(long var0, long var1) { return 0l; }
	public static double min(double var0, double var1) { return 0.0d; }
	public static float min(float var0, float var1) { return 0.0f; }
	public static int min(int var0, int var1) { return 0; }
	public static long min(long var0, long var1) { return 0l; }
	public static native double pow(double var0, double var1);
	public static double random() { return 0.0d; }
	public static double rint(double var0) { return 0.0d; }
	public static long round(double var0) { return 0l; }
	public static int round(float var0) { return 0; }
	public static double signum(double var0) { return 0.0d; }
	public static float signum(float var0) { return 0.0f; }
	public static native double sin(double var0);
	public static native double sinh(double var0);
	public static native double sqrt(double var0);
	public static native double tan(double var0);
	public static native double tanh(double var0);
	public static double toDegrees(double var0) { return 0.0d; }
	public static double toRadians(double var0) { return 0.0d; }
	public static double ulp(double var0) { return 0.0d; }
	public static float ulp(float var0) { return 0.0f; }
	private StrictMath() { } /* generated constructor to prevent compiler adding default public constructor */
}

