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
public final class Float extends java.lang.Number implements java.lang.Comparable<java.lang.Float> {
	public final static int MAX_EXPONENT = 127;
	public final static float MAX_VALUE = 3.4028235E38f;
	public final static int MIN_EXPONENT = -126;
	public final static float MIN_NORMAL = 1.17549435E-38f;
	public final static float MIN_VALUE = 1.4E-45f;
	public final static float NEGATIVE_INFINITY = -1.0f / 0.0f;
	public final static float NaN = 0.0f / 0.0f;
	public final static float POSITIVE_INFINITY = 1.0f / 0.0f;
	public final static int SIZE = 32;
	public final static java.lang.Class<java.lang.Float> TYPE; static { TYPE = null; }
	public Float(double var0) { } 
	public Float(float var0) { } 
	public Float(java.lang.String var0) { } 
	public static int compare(float var0, float var1) { return 0; }
	public int compareTo(java.lang.Float var0) { return 0; }
	public double doubleValue() { return 0.0d; }
	public static int floatToIntBits(float var0) { return 0; }
	public static native int floatToRawIntBits(float var0);
	public float floatValue() { return 0.0f; }
	public static native float intBitsToFloat(int var0);
	public int intValue() { return 0; }
	public boolean isInfinite() { return false; }
	public static boolean isInfinite(float var0) { return false; }
	public boolean isNaN() { return false; }
	public static boolean isNaN(float var0) { return false; }
	public long longValue() { return 0l; }
	public static float parseFloat(java.lang.String var0) { return 0.0f; }
	public static java.lang.String toHexString(float var0) { return null; }
	public static java.lang.String toString(float var0) { return null; }
	public static java.lang.Float valueOf(float var0) { return null; }
	public static java.lang.Float valueOf(java.lang.String var0) { return null; }
}

