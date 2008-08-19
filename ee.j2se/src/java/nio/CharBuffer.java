/*
 * $Date$
 *
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
public abstract class CharBuffer extends java.nio.Buffer implements java.lang.CharSequence, java.lang.Comparable {
	public static java.nio.CharBuffer allocate(int var0) { return null; }
	public final char[] array() { return null; }
	public final int arrayOffset() { return 0; }
	public abstract java.nio.CharBuffer asReadOnlyBuffer();
	public final char charAt(int var0) { return '\0'; }
	public abstract java.nio.CharBuffer compact();
	public int compareTo(java.lang.Object var0) { return 0; }
	public abstract java.nio.CharBuffer duplicate();
	public abstract char get();
	public abstract char get(int var0);
	public java.nio.CharBuffer get(char[] var0) { return null; }
	public java.nio.CharBuffer get(char[] var0, int var1, int var2) { return null; }
	public final boolean hasArray() { return false; }
	public int hashCode() { return 0; }
	public abstract boolean isDirect();
	public final int length() { return 0; }
	public abstract java.nio.ByteOrder order();
	public abstract java.nio.CharBuffer put(char var0);
	public abstract java.nio.CharBuffer put(int var0, char var1);
	public final java.nio.CharBuffer put(java.lang.String var0) { return null; }
	public java.nio.CharBuffer put(java.lang.String var0, int var1, int var2) { return null; }
	public java.nio.CharBuffer put(java.nio.CharBuffer var0) { return null; }
	public final java.nio.CharBuffer put(char[] var0) { return null; }
	public java.nio.CharBuffer put(char[] var0, int var1, int var2) { return null; }
	public abstract java.nio.CharBuffer slice();
	public static java.nio.CharBuffer wrap(java.lang.CharSequence var0) { return null; }
	public static java.nio.CharBuffer wrap(java.lang.CharSequence var0, int var1, int var2) { return null; }
	public static java.nio.CharBuffer wrap(char[] var0) { return null; }
	public static java.nio.CharBuffer wrap(char[] var0, int var1, int var2) { return null; }
	CharBuffer() { } /* generated constructor to prevent compiler adding default public constructor */
}

