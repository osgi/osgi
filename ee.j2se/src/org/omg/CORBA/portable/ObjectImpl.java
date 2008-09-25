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

package org.omg.CORBA.portable;
public abstract class ObjectImpl implements org.omg.CORBA.Object {
	public ObjectImpl() { }
	public org.omg.CORBA.Request _create_request(org.omg.CORBA.Context var0, java.lang.String var1, org.omg.CORBA.NVList var2, org.omg.CORBA.NamedValue var3) { return null; }
	public org.omg.CORBA.Request _create_request(org.omg.CORBA.Context var0, java.lang.String var1, org.omg.CORBA.NVList var2, org.omg.CORBA.NamedValue var3, org.omg.CORBA.ExceptionList var4, org.omg.CORBA.ContextList var5) { return null; }
	public org.omg.CORBA.Object _duplicate() { return null; }
	public org.omg.CORBA.portable.Delegate _get_delegate() { return null; }
	public org.omg.CORBA.DomainManager[] _get_domain_managers() { return null; }
	public org.omg.CORBA.Object _get_interface_def() { return null; }
	public org.omg.CORBA.Policy _get_policy(int var0) { return null; }
	public int _hash(int var0) { return 0; }
	public abstract java.lang.String[] _ids();
	public org.omg.CORBA.portable.InputStream _invoke(org.omg.CORBA.portable.OutputStream var0) throws org.omg.CORBA.portable.ApplicationException, org.omg.CORBA.portable.RemarshalException { return null; }
	public boolean _is_a(java.lang.String var0) { return false; }
	public boolean _is_equivalent(org.omg.CORBA.Object var0) { return false; }
	public boolean _is_local() { return false; }
	public boolean _non_existent() { return false; }
	public org.omg.CORBA.ORB _orb() { return null; }
	public void _release() { }
	public void _releaseReply(org.omg.CORBA.portable.InputStream var0) { }
	public org.omg.CORBA.Request _request(java.lang.String var0) { return null; }
	public org.omg.CORBA.portable.OutputStream _request(java.lang.String var0, boolean var1) { return null; }
	public void _servant_postinvoke(org.omg.CORBA.portable.ServantObject var0) { }
	public org.omg.CORBA.portable.ServantObject _servant_preinvoke(java.lang.String var0, java.lang.Class var1) { return null; }
	public void _set_delegate(org.omg.CORBA.portable.Delegate var0) { }
	public org.omg.CORBA.Object _set_policy_override(org.omg.CORBA.Policy[] var0, org.omg.CORBA.SetOverrideType var1) { return null; }
	public int hashCode() { return 0; }
}

