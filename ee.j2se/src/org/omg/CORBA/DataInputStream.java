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
public interface DataInputStream extends org.omg.CORBA.portable.ValueBase {
	java.lang.Object read_Abstract();
	org.omg.CORBA.Object read_Object();
	org.omg.CORBA.TypeCode read_TypeCode();
	java.io.Serializable read_Value();
	org.omg.CORBA.Any read_any();
	void read_any_array(org.omg.CORBA.AnySeqHolder var0, int var1, int var2);
	boolean read_boolean();
	void read_boolean_array(org.omg.CORBA.BooleanSeqHolder var0, int var1, int var2);
	char read_char();
	void read_char_array(org.omg.CORBA.CharSeqHolder var0, int var1, int var2);
	double read_double();
	void read_double_array(org.omg.CORBA.DoubleSeqHolder var0, int var1, int var2);
	float read_float();
	void read_float_array(org.omg.CORBA.FloatSeqHolder var0, int var1, int var2);
	int read_long();
	void read_long_array(org.omg.CORBA.LongSeqHolder var0, int var1, int var2);
	long read_longlong();
	void read_longlong_array(org.omg.CORBA.LongLongSeqHolder var0, int var1, int var2);
	byte read_octet();
	void read_octet_array(org.omg.CORBA.OctetSeqHolder var0, int var1, int var2);
	short read_short();
	void read_short_array(org.omg.CORBA.ShortSeqHolder var0, int var1, int var2);
	java.lang.String read_string();
	int read_ulong();
	void read_ulong_array(org.omg.CORBA.ULongSeqHolder var0, int var1, int var2);
	long read_ulonglong();
	void read_ulonglong_array(org.omg.CORBA.ULongLongSeqHolder var0, int var1, int var2);
	short read_ushort();
	void read_ushort_array(org.omg.CORBA.UShortSeqHolder var0, int var1, int var2);
	char read_wchar();
	void read_wchar_array(org.omg.CORBA.WCharSeqHolder var0, int var1, int var2);
	java.lang.String read_wstring();
}

