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

package org.omg.PortableServer;
public abstract class Servant {
	public Servant() { } 
	public abstract java.lang.String[] _all_interfaces(org.omg.PortableServer.POA var0, byte[] var1);
	public org.omg.PortableServer.POA _default_POA() { return null; }
	public final org.omg.PortableServer.portable.Delegate _get_delegate() { return null; }
	public org.omg.CORBA.Object _get_interface_def() { return null; }
	public boolean _is_a(java.lang.String var0) { return false; }
	public boolean _non_existent() { return false; }
	public final byte[] _object_id() { return null; }
	public final org.omg.CORBA.ORB _orb() { return null; }
	public final org.omg.PortableServer.POA _poa() { return null; }
	public final void _set_delegate(org.omg.PortableServer.portable.Delegate var0) { }
	public final org.omg.CORBA.Object _this_object() { return null; }
	public final org.omg.CORBA.Object _this_object(org.omg.CORBA.ORB var0) { return null; }
}

