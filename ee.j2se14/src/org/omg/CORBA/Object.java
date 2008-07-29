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
public abstract interface Object {
	public abstract org.omg.CORBA.Request _create_request(org.omg.CORBA.Context var0, java.lang.String var1, org.omg.CORBA.NVList var2, org.omg.CORBA.NamedValue var3);
	public abstract org.omg.CORBA.Request _create_request(org.omg.CORBA.Context var0, java.lang.String var1, org.omg.CORBA.NVList var2, org.omg.CORBA.NamedValue var3, org.omg.CORBA.ExceptionList var4, org.omg.CORBA.ContextList var5);
	public abstract org.omg.CORBA.Object _duplicate();
	public abstract org.omg.CORBA.DomainManager[] _get_domain_managers();
	public abstract org.omg.CORBA.Object _get_interface_def();
	public abstract org.omg.CORBA.Policy _get_policy(int var0);
	public abstract int _hash(int var0);
	public abstract boolean _is_a(java.lang.String var0);
	public abstract boolean _is_equivalent(org.omg.CORBA.Object var0);
	public abstract boolean _non_existent();
	public abstract void _release();
	public abstract org.omg.CORBA.Request _request(java.lang.String var0);
	public abstract org.omg.CORBA.Object _set_policy_override(org.omg.CORBA.Policy[] var0, org.omg.CORBA.SetOverrideType var1);
}

