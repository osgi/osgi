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

package java.nio;
public abstract class DoubleBuffer extends java.nio.Buffer implements java.lang.Comparable<java.nio.DoubleBuffer> {
	public static java.nio.DoubleBuffer allocate(int var0) { return null; }
	public final double[] array() { return null; }
	public final int arrayOffset() { return 0; }
	public abstract java.nio.DoubleBuffer asReadOnlyBuffer();
	public abstract java.nio.DoubleBuffer compact();
	public int compareTo(java.nio.DoubleBuffer var0) { return 0; }
	public abstract java.nio.DoubleBuffer duplicate();
	public abstract double get();
	public abstract double get(int var0);
	public java.nio.DoubleBuffer get(double[] var0) { return null; }
	public java.nio.DoubleBuffer get(double[] var0, int var1, int var2) { return null; }
	public final boolean hasArray() { return false; }
	public abstract boolean isDirect();
	public abstract java.nio.ByteOrder order();
	public abstract java.nio.DoubleBuffer put(double var0);
	public abstract java.nio.DoubleBuffer put(int var0, double var1);
	public java.nio.DoubleBuffer put(java.nio.DoubleBuffer var0) { return null; }
	public final java.nio.DoubleBuffer put(double[] var0) { return null; }
	public java.nio.DoubleBuffer put(double[] var0, int var1, int var2) { return null; }
	public abstract java.nio.DoubleBuffer slice();
	public static java.nio.DoubleBuffer wrap(double[] var0) { return null; }
	public static java.nio.DoubleBuffer wrap(double[] var0, int var1, int var2) { return null; }
	private DoubleBuffer() { } /* generated constructor to prevent compiler adding default public constructor */
}

