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
public abstract class OutputStream extends java.io.OutputStream {
	public OutputStream() { }
	public abstract org.omg.CORBA.portable.InputStream create_input_stream();
	public org.omg.CORBA.ORB orb() { return null; }
	public void write(int var0) throws java.io.IOException { }
	public void write_Context(org.omg.CORBA.Context var0, org.omg.CORBA.ContextList var1) { }
	public abstract void write_Object(org.omg.CORBA.Object var0);
	/** @deprecated */ public void write_Principal(org.omg.CORBA.Principal var0) { }
	public abstract void write_TypeCode(org.omg.CORBA.TypeCode var0);
	public abstract void write_any(org.omg.CORBA.Any var0);
	public abstract void write_boolean(boolean var0);
	public abstract void write_boolean_array(boolean[] var0, int var1, int var2);
	public abstract void write_char(char var0);
	public abstract void write_char_array(char[] var0, int var1, int var2);
	public abstract void write_double(double var0);
	public abstract void write_double_array(double[] var0, int var1, int var2);
	public void write_fixed(java.math.BigDecimal var0) { }
	public abstract void write_float(float var0);
	public abstract void write_float_array(float[] var0, int var1, int var2);
	public abstract void write_long(int var0);
	public abstract void write_long_array(int[] var0, int var1, int var2);
	public abstract void write_longlong(long var0);
	public abstract void write_longlong_array(long[] var0, int var1, int var2);
	public abstract void write_octet(byte var0);
	public abstract void write_octet_array(byte[] var0, int var1, int var2);
	public abstract void write_short(short var0);
	public abstract void write_short_array(short[] var0, int var1, int var2);
	public abstract void write_string(java.lang.String var0);
	public abstract void write_ulong(int var0);
	public abstract void write_ulong_array(int[] var0, int var1, int var2);
	public abstract void write_ulonglong(long var0);
	public abstract void write_ulonglong_array(long[] var0, int var1, int var2);
	public abstract void write_ushort(short var0);
	public abstract void write_ushort_array(short[] var0, int var1, int var2);
	public abstract void write_wchar(char var0);
	public abstract void write_wchar_array(char[] var0, int var1, int var2);
	public abstract void write_wstring(java.lang.String var0);
}

