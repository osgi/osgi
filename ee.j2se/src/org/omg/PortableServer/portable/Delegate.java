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

package org.omg.PortableServer.portable;
public interface Delegate {
	org.omg.PortableServer.POA default_POA(org.omg.PortableServer.Servant var0);
	org.omg.CORBA.Object get_interface_def(org.omg.PortableServer.Servant var0);
	boolean is_a(org.omg.PortableServer.Servant var0, java.lang.String var1);
	boolean non_existent(org.omg.PortableServer.Servant var0);
	byte[] object_id(org.omg.PortableServer.Servant var0);
	org.omg.CORBA.ORB orb(org.omg.PortableServer.Servant var0);
	org.omg.PortableServer.POA poa(org.omg.PortableServer.Servant var0);
	org.omg.CORBA.Object this_object(org.omg.PortableServer.Servant var0);
}

