/*
 * $Revision$
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

package org.omg.CORBA;
public abstract interface DataInputStream extends org.omg.CORBA.portable.ValueBase {
	public abstract java.lang.Object read_Abstract();
	public abstract org.omg.CORBA.Object read_Object();
	public abstract org.omg.CORBA.TypeCode read_TypeCode();
	public abstract java.io.Serializable read_Value();
	public abstract org.omg.CORBA.Any read_any();
	public abstract void read_any_array(org.omg.CORBA.AnySeqHolder var0, int var1, int var2);
	public abstract boolean read_boolean();
	public abstract void read_boolean_array(org.omg.CORBA.BooleanSeqHolder var0, int var1, int var2);
	public abstract char read_char();
	public abstract void read_char_array(org.omg.CORBA.CharSeqHolder var0, int var1, int var2);
	public abstract double read_double();
	public abstract void read_double_array(org.omg.CORBA.DoubleSeqHolder var0, int var1, int var2);
	public abstract float read_float();
	public abstract void read_float_array(org.omg.CORBA.FloatSeqHolder var0, int var1, int var2);
	public abstract int read_long();
	public abstract void read_long_array(org.omg.CORBA.LongSeqHolder var0, int var1, int var2);
	public abstract long read_longlong();
	public abstract void read_longlong_array(org.omg.CORBA.LongLongSeqHolder var0, int var1, int var2);
	public abstract byte read_octet();
	public abstract void read_octet_array(org.omg.CORBA.OctetSeqHolder var0, int var1, int var2);
	public abstract short read_short();
	public abstract void read_short_array(org.omg.CORBA.ShortSeqHolder var0, int var1, int var2);
	public abstract java.lang.String read_string();
	public abstract int read_ulong();
	public abstract void read_ulong_array(org.omg.CORBA.ULongSeqHolder var0, int var1, int var2);
	public abstract long read_ulonglong();
	public abstract void read_ulonglong_array(org.omg.CORBA.ULongLongSeqHolder var0, int var1, int var2);
	public abstract short read_ushort();
	public abstract void read_ushort_array(org.omg.CORBA.UShortSeqHolder var0, int var1, int var2);
	public abstract char read_wchar();
	public abstract void read_wchar_array(org.omg.CORBA.WCharSeqHolder var0, int var1, int var2);
	public abstract java.lang.String read_wstring();
}

