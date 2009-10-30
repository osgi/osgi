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

package java.lang.reflect;
public final class Array {
	public static native java.lang.Object get(java.lang.Object var0, int var1);
	public static native boolean getBoolean(java.lang.Object var0, int var1);
	public static native byte getByte(java.lang.Object var0, int var1);
	public static native char getChar(java.lang.Object var0, int var1);
	public static native double getDouble(java.lang.Object var0, int var1);
	public static native float getFloat(java.lang.Object var0, int var1);
	public static native int getInt(java.lang.Object var0, int var1);
	public static native int getLength(java.lang.Object var0);
	public static native long getLong(java.lang.Object var0, int var1);
	public static native short getShort(java.lang.Object var0, int var1);
	public static java.lang.Object newInstance(java.lang.Class<?> var0, int var1) { return null; }
	public static java.lang.Object newInstance(java.lang.Class<?> var0, int[] var1) { return null; }
	public static native void set(java.lang.Object var0, int var1, java.lang.Object var2);
	public static native void setBoolean(java.lang.Object var0, int var1, boolean var2);
	public static native void setByte(java.lang.Object var0, int var1, byte var2);
	public static native void setChar(java.lang.Object var0, int var1, char var2);
	public static native void setDouble(java.lang.Object var0, int var1, double var2);
	public static native void setFloat(java.lang.Object var0, int var1, float var2);
	public static native void setInt(java.lang.Object var0, int var1, int var2);
	public static native void setLong(java.lang.Object var0, int var1, long var2);
	public static native void setShort(java.lang.Object var0, int var1, short var2);
	private Array() { } /* generated constructor to prevent compiler adding default public constructor */
}

