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

package org.omg.DynamicAny;
public interface DynAnyOperations {
	void assign(org.omg.DynamicAny.DynAny var0) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	int component_count();
	org.omg.DynamicAny.DynAny copy();
	org.omg.DynamicAny.DynAny current_component() throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void destroy();
	boolean equal(org.omg.DynamicAny.DynAny var0);
	void from_any(org.omg.CORBA.Any var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	org.omg.CORBA.Any get_any() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	boolean get_boolean() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	char get_char() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	double get_double() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	org.omg.DynamicAny.DynAny get_dyn_any() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	float get_float() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	int get_long() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	long get_longlong() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	byte get_octet() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	org.omg.CORBA.Object get_reference() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	short get_short() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	java.lang.String get_string() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	org.omg.CORBA.TypeCode get_typecode() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	int get_ulong() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	long get_ulonglong() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	short get_ushort() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	java.io.Serializable get_val() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	char get_wchar() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	java.lang.String get_wstring() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_any(org.omg.CORBA.Any var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_boolean(boolean var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_char(char var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_double(double var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_dyn_any(org.omg.DynamicAny.DynAny var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_float(float var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_long(int var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_longlong(long var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_octet(byte var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_reference(org.omg.CORBA.Object var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_short(short var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_string(java.lang.String var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_typecode(org.omg.CORBA.TypeCode var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_ulong(int var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_ulonglong(long var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_ushort(short var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_val(java.io.Serializable var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_wchar(char var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void insert_wstring(java.lang.String var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	boolean next();
	void rewind();
	boolean seek(int var0);
	org.omg.CORBA.Any to_any();
	org.omg.CORBA.TypeCode type();
}

