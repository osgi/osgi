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
public abstract class ByteBuffer extends java.nio.Buffer implements java.lang.Comparable<java.nio.ByteBuffer> {
	public static java.nio.ByteBuffer allocate(int var0) { return null; }
	public static java.nio.ByteBuffer allocateDirect(int var0) { return null; }
	public final byte[] array() { return null; }
	public final int arrayOffset() { return 0; }
	public abstract java.nio.CharBuffer asCharBuffer();
	public abstract java.nio.DoubleBuffer asDoubleBuffer();
	public abstract java.nio.FloatBuffer asFloatBuffer();
	public abstract java.nio.IntBuffer asIntBuffer();
	public abstract java.nio.LongBuffer asLongBuffer();
	public abstract java.nio.ByteBuffer asReadOnlyBuffer();
	public abstract java.nio.ShortBuffer asShortBuffer();
	public abstract java.nio.ByteBuffer compact();
	public int compareTo(java.nio.ByteBuffer var0) { return 0; }
	public abstract java.nio.ByteBuffer duplicate();
	public abstract byte get();
	public abstract byte get(int var0);
	public java.nio.ByteBuffer get(byte[] var0) { return null; }
	public java.nio.ByteBuffer get(byte[] var0, int var1, int var2) { return null; }
	public abstract char getChar();
	public abstract char getChar(int var0);
	public abstract double getDouble();
	public abstract double getDouble(int var0);
	public abstract float getFloat();
	public abstract float getFloat(int var0);
	public abstract int getInt();
	public abstract int getInt(int var0);
	public abstract long getLong();
	public abstract long getLong(int var0);
	public abstract short getShort();
	public abstract short getShort(int var0);
	public final boolean hasArray() { return false; }
	public int hashCode() { return 0; }
	public abstract boolean isDirect();
	public final java.nio.ByteOrder order() { return null; }
	public final java.nio.ByteBuffer order(java.nio.ByteOrder var0) { return null; }
	public abstract java.nio.ByteBuffer put(byte var0);
	public abstract java.nio.ByteBuffer put(int var0, byte var1);
	public java.nio.ByteBuffer put(java.nio.ByteBuffer var0) { return null; }
	public final java.nio.ByteBuffer put(byte[] var0) { return null; }
	public java.nio.ByteBuffer put(byte[] var0, int var1, int var2) { return null; }
	public abstract java.nio.ByteBuffer putChar(char var0);
	public abstract java.nio.ByteBuffer putChar(int var0, char var1);
	public abstract java.nio.ByteBuffer putDouble(double var0);
	public abstract java.nio.ByteBuffer putDouble(int var0, double var1);
	public abstract java.nio.ByteBuffer putFloat(float var0);
	public abstract java.nio.ByteBuffer putFloat(int var0, float var1);
	public abstract java.nio.ByteBuffer putInt(int var0);
	public abstract java.nio.ByteBuffer putInt(int var0, int var1);
	public abstract java.nio.ByteBuffer putLong(int var0, long var1);
	public abstract java.nio.ByteBuffer putLong(long var0);
	public abstract java.nio.ByteBuffer putShort(int var0, short var1);
	public abstract java.nio.ByteBuffer putShort(short var0);
	public abstract java.nio.ByteBuffer slice();
	public static java.nio.ByteBuffer wrap(byte[] var0) { return null; }
	public static java.nio.ByteBuffer wrap(byte[] var0, int var1, int var2) { return null; }
	ByteBuffer() { } /* generated constructor to prevent compiler adding default public constructor */
}

