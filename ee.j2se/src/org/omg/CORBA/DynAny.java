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
public abstract interface DynAny extends org.omg.CORBA.Object {
	public abstract void assign(org.omg.CORBA.DynAny var0) throws org.omg.CORBA.DynAnyPackage.Invalid;
	public abstract org.omg.CORBA.DynAny copy();
	public abstract org.omg.CORBA.DynAny current_component();
	public abstract void destroy();
	public abstract void from_any(org.omg.CORBA.Any var0) throws org.omg.CORBA.DynAnyPackage.Invalid;
	public abstract org.omg.CORBA.Any get_any() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract boolean get_boolean() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract char get_char() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract double get_double() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract float get_float() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract int get_long() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract long get_longlong() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract byte get_octet() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract org.omg.CORBA.Object get_reference() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract short get_short() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract java.lang.String get_string() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract org.omg.CORBA.TypeCode get_typecode() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract int get_ulong() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract long get_ulonglong() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract short get_ushort() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract java.io.Serializable get_val() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract char get_wchar() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract java.lang.String get_wstring() throws org.omg.CORBA.DynAnyPackage.TypeMismatch;
	public abstract void insert_any(org.omg.CORBA.Any var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_boolean(boolean var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_char(char var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_double(double var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_float(float var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_long(int var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_longlong(long var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_octet(byte var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_reference(org.omg.CORBA.Object var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_short(short var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_string(java.lang.String var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_typecode(org.omg.CORBA.TypeCode var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_ulong(int var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_ulonglong(long var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_ushort(short var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_val(java.io.Serializable var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_wchar(char var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract void insert_wstring(java.lang.String var0) throws org.omg.CORBA.DynAnyPackage.InvalidValue;
	public abstract boolean next();
	public abstract void rewind();
	public abstract boolean seek(int var0);
	public abstract org.omg.CORBA.Any to_any() throws org.omg.CORBA.DynAnyPackage.Invalid;
	public abstract org.omg.CORBA.TypeCode type();
}

