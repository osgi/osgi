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

package org.omg.CORBA.portable;
public abstract class Delegate {
	public Delegate() { }
	public abstract org.omg.CORBA.Request create_request(org.omg.CORBA.Object var0, org.omg.CORBA.Context var1, java.lang.String var2, org.omg.CORBA.NVList var3, org.omg.CORBA.NamedValue var4);
	public abstract org.omg.CORBA.Request create_request(org.omg.CORBA.Object var0, org.omg.CORBA.Context var1, java.lang.String var2, org.omg.CORBA.NVList var3, org.omg.CORBA.NamedValue var4, org.omg.CORBA.ExceptionList var5, org.omg.CORBA.ContextList var6);
	public abstract org.omg.CORBA.Object duplicate(org.omg.CORBA.Object var0);
	public boolean equals(org.omg.CORBA.Object var0, java.lang.Object var1) { return false; }
	public org.omg.CORBA.DomainManager[] get_domain_managers(org.omg.CORBA.Object var0) { return null; }
	public abstract org.omg.CORBA.Object get_interface_def(org.omg.CORBA.Object var0);
	public org.omg.CORBA.Policy get_policy(org.omg.CORBA.Object var0, int var1) { return null; }
	public abstract int hash(org.omg.CORBA.Object var0, int var1);
	public int hashCode(org.omg.CORBA.Object var0) { return 0; }
	public org.omg.CORBA.portable.InputStream invoke(org.omg.CORBA.Object var0, org.omg.CORBA.portable.OutputStream var1) throws org.omg.CORBA.portable.ApplicationException, org.omg.CORBA.portable.RemarshalException { return null; }
	public abstract boolean is_a(org.omg.CORBA.Object var0, java.lang.String var1);
	public abstract boolean is_equivalent(org.omg.CORBA.Object var0, org.omg.CORBA.Object var1);
	public boolean is_local(org.omg.CORBA.Object var0) { return false; }
	public abstract boolean non_existent(org.omg.CORBA.Object var0);
	public org.omg.CORBA.ORB orb(org.omg.CORBA.Object var0) { return null; }
	public abstract void release(org.omg.CORBA.Object var0);
	public void releaseReply(org.omg.CORBA.Object var0, org.omg.CORBA.portable.InputStream var1) { }
	public abstract org.omg.CORBA.Request request(org.omg.CORBA.Object var0, java.lang.String var1);
	public org.omg.CORBA.portable.OutputStream request(org.omg.CORBA.Object var0, java.lang.String var1, boolean var2) { return null; }
	public void servant_postinvoke(org.omg.CORBA.Object var0, org.omg.CORBA.portable.ServantObject var1) { }
	public org.omg.CORBA.portable.ServantObject servant_preinvoke(org.omg.CORBA.Object var0, java.lang.String var1, java.lang.Class var2) { return null; }
	public org.omg.CORBA.Object set_policy_override(org.omg.CORBA.Object var0, org.omg.CORBA.Policy[] var1, org.omg.CORBA.SetOverrideType var2) { return null; }
	public java.lang.String toString(org.omg.CORBA.Object var0) { return null; }
}

