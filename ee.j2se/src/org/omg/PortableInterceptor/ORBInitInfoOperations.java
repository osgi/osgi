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
public interface ORBInitInfoOperations {
	void add_client_request_interceptor(org.omg.PortableInterceptor.ClientRequestInterceptor var0) throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
	void add_ior_interceptor(org.omg.PortableInterceptor.IORInterceptor var0) throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
	void add_server_request_interceptor(org.omg.PortableInterceptor.ServerRequestInterceptor var0) throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
	int allocate_slot_id();
	java.lang.String[] arguments();
	org.omg.IOP.CodecFactory codec_factory();
	java.lang.String orb_id();
	void register_initial_reference(java.lang.String var0, org.omg.CORBA.Object var1) throws org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;
	void register_policy_factory(int var0, org.omg.PortableInterceptor.PolicyFactory var1);
	org.omg.CORBA.Object resolve_initial_references(java.lang.String var0) throws org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;
}

