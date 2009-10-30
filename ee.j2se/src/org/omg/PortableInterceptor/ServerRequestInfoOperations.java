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

package org.omg.PortableInterceptor;
public interface ServerRequestInfoOperations extends org.omg.PortableInterceptor.RequestInfoOperations {
	byte[] adapter_id();
	java.lang.String[] adapter_name();
	void add_reply_service_context(org.omg.IOP.ServiceContext var0, boolean var1);
	org.omg.CORBA.Policy get_server_policy(int var0);
	byte[] object_id();
	java.lang.String orb_id();
	org.omg.CORBA.Any sending_exception();
	java.lang.String server_id();
	void set_slot(int var0, org.omg.CORBA.Any var1) throws org.omg.PortableInterceptor.InvalidSlot;
	boolean target_is_a(java.lang.String var0);
	java.lang.String target_most_derived_interface();
}

