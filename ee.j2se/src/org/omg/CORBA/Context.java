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
public abstract class Context {
	public Context() { }
	public abstract java.lang.String context_name();
	public abstract org.omg.CORBA.Context create_child(java.lang.String var0);
	public abstract void delete_values(java.lang.String var0);
	public abstract org.omg.CORBA.NVList get_values(java.lang.String var0, int var1, java.lang.String var2);
	public abstract org.omg.CORBA.Context parent();
	public abstract void set_one_value(java.lang.String var0, org.omg.CORBA.Any var1);
	public abstract void set_values(org.omg.CORBA.NVList var0);
}

