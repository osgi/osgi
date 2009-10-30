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
public interface DynUnionOperations extends org.omg.DynamicAny.DynAnyOperations {
	org.omg.CORBA.TCKind discriminator_kind();
	org.omg.DynamicAny.DynAny get_discriminator();
	boolean has_no_active_member();
	org.omg.DynamicAny.DynAny member() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;
	org.omg.CORBA.TCKind member_kind() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;
	java.lang.String member_name() throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;
	void set_discriminator(org.omg.DynamicAny.DynAny var0) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void set_to_default_member() throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
	void set_to_no_active_member() throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
}

