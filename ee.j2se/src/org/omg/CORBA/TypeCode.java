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

package org.omg.CORBA;
public abstract class TypeCode implements org.omg.CORBA.portable.IDLEntity {
	public TypeCode() { }
	public abstract org.omg.CORBA.TypeCode concrete_base_type() throws org.omg.CORBA.TypeCodePackage.BadKind;
	public abstract org.omg.CORBA.TypeCode content_type() throws org.omg.CORBA.TypeCodePackage.BadKind;
	public abstract int default_index() throws org.omg.CORBA.TypeCodePackage.BadKind;
	public abstract org.omg.CORBA.TypeCode discriminator_type() throws org.omg.CORBA.TypeCodePackage.BadKind;
	public abstract boolean equal(org.omg.CORBA.TypeCode var0);
	public abstract boolean equivalent(org.omg.CORBA.TypeCode var0);
	public abstract short fixed_digits() throws org.omg.CORBA.TypeCodePackage.BadKind;
	public abstract short fixed_scale() throws org.omg.CORBA.TypeCodePackage.BadKind;
	public abstract org.omg.CORBA.TypeCode get_compact_typecode();
	public abstract java.lang.String id() throws org.omg.CORBA.TypeCodePackage.BadKind;
	public abstract org.omg.CORBA.TCKind kind();
	public abstract int length() throws org.omg.CORBA.TypeCodePackage.BadKind;
	public abstract int member_count() throws org.omg.CORBA.TypeCodePackage.BadKind;
	public abstract org.omg.CORBA.Any member_label(int var0) throws org.omg.CORBA.TypeCodePackage.BadKind, org.omg.CORBA.TypeCodePackage.Bounds;
	public abstract java.lang.String member_name(int var0) throws org.omg.CORBA.TypeCodePackage.BadKind, org.omg.CORBA.TypeCodePackage.Bounds;
	public abstract org.omg.CORBA.TypeCode member_type(int var0) throws org.omg.CORBA.TypeCodePackage.BadKind, org.omg.CORBA.TypeCodePackage.Bounds;
	public abstract short member_visibility(int var0) throws org.omg.CORBA.TypeCodePackage.BadKind, org.omg.CORBA.TypeCodePackage.Bounds;
	public abstract java.lang.String name() throws org.omg.CORBA.TypeCodePackage.BadKind;
	public abstract short type_modifier() throws org.omg.CORBA.TypeCodePackage.BadKind;
}

