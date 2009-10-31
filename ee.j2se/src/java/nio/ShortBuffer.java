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
public abstract class ShortBuffer extends java.nio.Buffer implements java.lang.Comparable<java.nio.ShortBuffer> {
	public static java.nio.ShortBuffer allocate(int var0) { return null; }
	public final short[] array() { return null; }
	public final int arrayOffset() { return 0; }
	public abstract java.nio.ShortBuffer asReadOnlyBuffer();
	public abstract java.nio.ShortBuffer compact();
	public int compareTo(java.nio.ShortBuffer var0) { return 0; }
	public abstract java.nio.ShortBuffer duplicate();
	public abstract short get();
	public abstract short get(int var0);
	public java.nio.ShortBuffer get(short[] var0) { return null; }
	public java.nio.ShortBuffer get(short[] var0, int var1, int var2) { return null; }
	public final boolean hasArray() { return false; }
	public abstract java.nio.ByteOrder order();
	public abstract java.nio.ShortBuffer put(int var0, short var1);
	public java.nio.ShortBuffer put(java.nio.ShortBuffer var0) { return null; }
	public abstract java.nio.ShortBuffer put(short var0);
	public final java.nio.ShortBuffer put(short[] var0) { return null; }
	public java.nio.ShortBuffer put(short[] var0, int var1, int var2) { return null; }
	public abstract java.nio.ShortBuffer slice();
	public static java.nio.ShortBuffer wrap(short[] var0) { return null; }
	public static java.nio.ShortBuffer wrap(short[] var0, int var1, int var2) { return null; }
	private ShortBuffer() { } /* generated constructor to prevent compiler adding default public constructor */
}

