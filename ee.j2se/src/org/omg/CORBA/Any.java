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

package org.omg.CORBA;
public abstract class Any implements org.omg.CORBA.portable.IDLEntity {
	public Any() { }
	public abstract org.omg.CORBA.portable.InputStream create_input_stream();
	public abstract org.omg.CORBA.portable.OutputStream create_output_stream();
	public abstract boolean equal(org.omg.CORBA.Any var0);
	public abstract org.omg.CORBA.Object extract_Object();
	/** @deprecated */ public org.omg.CORBA.Principal extract_Principal() { return null; }
	public org.omg.CORBA.portable.Streamable extract_Streamable() { return null; }
	public abstract org.omg.CORBA.TypeCode extract_TypeCode();
	public abstract java.io.Serializable extract_Value();
	public abstract org.omg.CORBA.Any extract_any();
	public abstract boolean extract_boolean();
	public abstract char extract_char();
	public abstract double extract_double();
	public java.math.BigDecimal extract_fixed() { return null; }
	public abstract float extract_float();
	public abstract int extract_long();
	public abstract long extract_longlong();
	public abstract byte extract_octet();
	public abstract short extract_short();
	public abstract java.lang.String extract_string();
	public abstract int extract_ulong();
	public abstract long extract_ulonglong();
	public abstract short extract_ushort();
	public abstract char extract_wchar();
	public abstract java.lang.String extract_wstring();
	public abstract void insert_Object(org.omg.CORBA.Object var0);
	public abstract void insert_Object(org.omg.CORBA.Object var0, org.omg.CORBA.TypeCode var1);
	/** @deprecated */ public void insert_Principal(org.omg.CORBA.Principal var0) { }
	public void insert_Streamable(org.omg.CORBA.portable.Streamable var0) { }
	public abstract void insert_TypeCode(org.omg.CORBA.TypeCode var0);
	public abstract void insert_Value(java.io.Serializable var0);
	public abstract void insert_Value(java.io.Serializable var0, org.omg.CORBA.TypeCode var1);
	public abstract void insert_any(org.omg.CORBA.Any var0);
	public abstract void insert_boolean(boolean var0);
	public abstract void insert_char(char var0);
	public abstract void insert_double(double var0);
	public void insert_fixed(java.math.BigDecimal var0) { }
	public void insert_fixed(java.math.BigDecimal var0, org.omg.CORBA.TypeCode var1) { }
	public abstract void insert_float(float var0);
	public abstract void insert_long(int var0);
	public abstract void insert_longlong(long var0);
	public abstract void insert_octet(byte var0);
	public abstract void insert_short(short var0);
	public abstract void insert_string(java.lang.String var0);
	public abstract void insert_ulong(int var0);
	public abstract void insert_ulonglong(long var0);
	public abstract void insert_ushort(short var0);
	public abstract void insert_wchar(char var0);
	public abstract void insert_wstring(java.lang.String var0);
	public abstract void read_value(org.omg.CORBA.portable.InputStream var0, org.omg.CORBA.TypeCode var1);
	public abstract org.omg.CORBA.TypeCode type();
	public abstract void type(org.omg.CORBA.TypeCode var0);
	public abstract void write_value(org.omg.CORBA.portable.OutputStream var0);
}

