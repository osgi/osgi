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
/** @deprecated */ public interface DynValue extends org.omg.CORBA.DynAny, org.omg.CORBA.Object {
	org.omg.CORBA.TCKind current_member_kind();
	java.lang.String current_member_name();
	org.omg.CORBA.NameValuePair[] get_members();
	void set_members(org.omg.CORBA.NameValuePair[] var0) throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}

