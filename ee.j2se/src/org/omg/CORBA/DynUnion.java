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
/** @deprecated */
@java.lang.Deprecated
public interface DynUnion extends org.omg.CORBA.DynAny, org.omg.CORBA.Object {
	org.omg.CORBA.DynAny discriminator();
	org.omg.CORBA.TCKind discriminator_kind();
	org.omg.CORBA.DynAny member();
	org.omg.CORBA.TCKind member_kind();
	java.lang.String member_name();
	void member_name(java.lang.String var0);
	boolean set_as_default();
	void set_as_default(boolean var0);
}

