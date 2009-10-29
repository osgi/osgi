/*
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package java.nio;
public abstract class FloatBuffer extends java.nio.Buffer implements java.lang.Comparable {
	public static java.nio.FloatBuffer allocate(int var0) { return null; }
	public final float[] array() { return null; }
	public final int arrayOffset() { return 0; }
	public abstract java.nio.FloatBuffer asReadOnlyBuffer();
	public abstract java.nio.FloatBuffer compact();
	public int compareTo(java.lang.Object var0) { return 0; }
	public abstract java.nio.FloatBuffer duplicate();
	public abstract float get();
	public abstract float get(int var0);
	public java.nio.FloatBuffer get(float[] var0) { return null; }
	public java.nio.FloatBuffer get(float[] var0, int var1, int var2) { return null; }
	public final boolean hasArray() { return false; }
	public int hashCode() { return 0; }
	public abstract boolean isDirect();
	public abstract java.nio.ByteOrder order();
	public abstract java.nio.FloatBuffer put(float var0);
	public abstract java.nio.FloatBuffer put(int var0, float var1);
	public java.nio.FloatBuffer put(java.nio.FloatBuffer var0) { return null; }
	public final java.nio.FloatBuffer put(float[] var0) { return null; }
	public java.nio.FloatBuffer put(float[] var0, int var1, int var2) { return null; }
	public abstract java.nio.FloatBuffer slice();
	public static java.nio.FloatBuffer wrap(float[] var0) { return null; }
	public static java.nio.FloatBuffer wrap(float[] var0, int var1, int var2) { return null; }
	FloatBuffer() { } /* generated constructor to prevent compiler adding default public constructor */
}

