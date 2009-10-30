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
public final class Double extends java.lang.Number implements java.lang.Comparable<java.lang.Double> {
	public final static double MAX_VALUE = 1.7976931348623157E308d;
	public final static double MIN_VALUE = 4.9E-324d;
	public final static double NEGATIVE_INFINITY = -1.0d / 0.0d;
	public final static double NaN = 0.0d / 0.0d;
	public final static double POSITIVE_INFINITY = 1.0d / 0.0d;
	public final static int SIZE = 64;
	public final static java.lang.Class<java.lang.Double> TYPE; static { TYPE = null; }
	public Double(double var0) { } 
	public Double(java.lang.String var0) { } 
	public static int compare(double var0, double var1) { return 0; }
	public int compareTo(java.lang.Double var0) { return 0; }
	public static native long doubleToLongBits(double var0);
	public static native long doubleToRawLongBits(double var0);
	public double doubleValue() { return 0.0d; }
	public float floatValue() { return 0.0f; }
	public int hashCode() { return 0; }
	public int intValue() { return 0; }
	public boolean isInfinite() { return false; }
	public static boolean isInfinite(double var0) { return false; }
	public boolean isNaN() { return false; }
	public static boolean isNaN(double var0) { return false; }
	public static native double longBitsToDouble(long var0);
	public long longValue() { return 0l; }
	public static double parseDouble(java.lang.String var0) { return 0.0d; }
	public static java.lang.String toHexString(double var0) { return null; }
	public static java.lang.String toString(double var0) { return null; }
	public static java.lang.Double valueOf(double var0) { return null; }
	public static java.lang.Double valueOf(java.lang.String var0) { return null; }
}

