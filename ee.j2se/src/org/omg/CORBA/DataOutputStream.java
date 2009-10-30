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

package org.omg.CORBA;
public interface DataOutputStream extends org.omg.CORBA.portable.ValueBase {
	void write_Abstract(java.lang.Object var0);
	void write_Object(org.omg.CORBA.Object var0);
	void write_TypeCode(org.omg.CORBA.TypeCode var0);
	void write_Value(java.io.Serializable var0);
	void write_any(org.omg.CORBA.Any var0);
	void write_any_array(org.omg.CORBA.Any[] var0, int var1, int var2);
	void write_boolean(boolean var0);
	void write_boolean_array(boolean[] var0, int var1, int var2);
	void write_char(char var0);
	void write_char_array(char[] var0, int var1, int var2);
	void write_double(double var0);
	void write_double_array(double[] var0, int var1, int var2);
	void write_float(float var0);
	void write_float_array(float[] var0, int var1, int var2);
	void write_long(int var0);
	void write_long_array(int[] var0, int var1, int var2);
	void write_longlong(long var0);
	void write_longlong_array(long[] var0, int var1, int var2);
	void write_octet(byte var0);
	void write_octet_array(byte[] var0, int var1, int var2);
	void write_short(short var0);
	void write_short_array(short[] var0, int var1, int var2);
	void write_string(java.lang.String var0);
	void write_ulong(int var0);
	void write_ulong_array(int[] var0, int var1, int var2);
	void write_ulonglong(long var0);
	void write_ulonglong_array(long[] var0, int var1, int var2);
	void write_ushort(short var0);
	void write_ushort_array(short[] var0, int var1, int var2);
	void write_wchar(char var0);
	void write_wchar_array(char[] var0, int var1, int var2);
	void write_wstring(java.lang.String var0);
}

