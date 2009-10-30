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
public interface DynValueOperations extends org.omg.DynamicAny.DynValueCommonOperations {
	org.omg.CORBA.TCKind current_member_kind() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	java.lang.String current_member_name() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	org.omg.DynamicAny.NameValuePair[] get_members() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;
	org.omg.DynamicAny.NameDynAnyPair[] get_members_as_dyn_any() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;
	void set_members(org.omg.DynamicAny.NameValuePair[] var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void set_members_as_dyn_any(org.omg.DynamicAny.NameDynAnyPair[] var0) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue, org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
}

