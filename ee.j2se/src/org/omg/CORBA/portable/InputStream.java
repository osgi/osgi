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

package org.omg.CORBA.portable;
public abstract class InputStream extends java.io.InputStream {
	public InputStream() { }
	public org.omg.CORBA.ORB orb() { return null; }
	public int read() throws java.io.IOException { return 0; }
	public org.omg.CORBA.Context read_Context() { return null; }
	public abstract org.omg.CORBA.Object read_Object();
	public org.omg.CORBA.Object read_Object(java.lang.Class var0) { return null; }
	/** @deprecated */ public org.omg.CORBA.Principal read_Principal() { return null; }
	public abstract org.omg.CORBA.TypeCode read_TypeCode();
	public abstract org.omg.CORBA.Any read_any();
	public abstract boolean read_boolean();
	public abstract void read_boolean_array(boolean[] var0, int var1, int var2);
	public abstract char read_char();
	public abstract void read_char_array(char[] var0, int var1, int var2);
	public abstract double read_double();
	public abstract void read_double_array(double[] var0, int var1, int var2);
	public java.math.BigDecimal read_fixed() { return null; }
	public abstract float read_float();
	public abstract void read_float_array(float[] var0, int var1, int var2);
	public abstract int read_long();
	public abstract void read_long_array(int[] var0, int var1, int var2);
	public abstract long read_longlong();
	public abstract void read_longlong_array(long[] var0, int var1, int var2);
	public abstract byte read_octet();
	public abstract void read_octet_array(byte[] var0, int var1, int var2);
	public abstract short read_short();
	public abstract void read_short_array(short[] var0, int var1, int var2);
	public abstract java.lang.String read_string();
	public abstract int read_ulong();
	public abstract void read_ulong_array(int[] var0, int var1, int var2);
	public abstract long read_ulonglong();
	public abstract void read_ulonglong_array(long[] var0, int var1, int var2);
	public abstract short read_ushort();
	public abstract void read_ushort_array(short[] var0, int var1, int var2);
	public abstract char read_wchar();
	public abstract void read_wchar_array(char[] var0, int var1, int var2);
	public abstract java.lang.String read_wstring();
}

