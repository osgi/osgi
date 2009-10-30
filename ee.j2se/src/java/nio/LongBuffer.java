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
public abstract class LongBuffer extends java.nio.Buffer implements java.lang.Comparable<java.nio.LongBuffer> {
	public static java.nio.LongBuffer allocate(int var0) { return null; }
	public final long[] array() { return null; }
	public final int arrayOffset() { return 0; }
	public abstract java.nio.LongBuffer asReadOnlyBuffer();
	public abstract java.nio.LongBuffer compact();
	public int compareTo(java.nio.LongBuffer var0) { return 0; }
	public abstract java.nio.LongBuffer duplicate();
	public abstract long get();
	public abstract long get(int var0);
	public java.nio.LongBuffer get(long[] var0) { return null; }
	public java.nio.LongBuffer get(long[] var0, int var1, int var2) { return null; }
	public final boolean hasArray() { return false; }
	public int hashCode() { return 0; }
	public abstract boolean isDirect();
	public abstract java.nio.ByteOrder order();
	public abstract java.nio.LongBuffer put(int var0, long var1);
	public abstract java.nio.LongBuffer put(long var0);
	public java.nio.LongBuffer put(java.nio.LongBuffer var0) { return null; }
	public final java.nio.LongBuffer put(long[] var0) { return null; }
	public java.nio.LongBuffer put(long[] var0, int var1, int var2) { return null; }
	public abstract java.nio.LongBuffer slice();
	public static java.nio.LongBuffer wrap(long[] var0) { return null; }
	public static java.nio.LongBuffer wrap(long[] var0, int var1, int var2) { return null; }
	LongBuffer() { } /* generated constructor to prevent compiler adding default public constructor */
}

