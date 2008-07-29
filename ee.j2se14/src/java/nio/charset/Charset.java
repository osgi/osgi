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

package java.nio.charset;
public abstract class Charset implements java.lang.Comparable {
	protected Charset(java.lang.String var0, java.lang.String[] var1) { }
	public final java.util.Set aliases() { return null; }
	public static java.util.SortedMap availableCharsets() { return null; }
	public boolean canEncode() { return false; }
	public final int compareTo(java.lang.Object var0) { return 0; }
	public abstract boolean contains(java.nio.charset.Charset var0);
	public final java.nio.CharBuffer decode(java.nio.ByteBuffer var0) { return null; }
	public java.lang.String displayName() { return null; }
	public java.lang.String displayName(java.util.Locale var0) { return null; }
	public final java.nio.ByteBuffer encode(java.lang.String var0) { return null; }
	public final java.nio.ByteBuffer encode(java.nio.CharBuffer var0) { return null; }
	public final boolean equals(java.lang.Object var0) { return false; }
	public static java.nio.charset.Charset forName(java.lang.String var0) { return null; }
	public final int hashCode() { return 0; }
	public final boolean isRegistered() { return false; }
	public static boolean isSupported(java.lang.String var0) { return false; }
	public final java.lang.String name() { return null; }
	public abstract java.nio.charset.CharsetDecoder newDecoder();
	public abstract java.nio.charset.CharsetEncoder newEncoder();
	public final java.lang.String toString() { return null; }
}

