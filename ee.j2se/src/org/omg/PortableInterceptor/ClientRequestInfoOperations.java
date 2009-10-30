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
public interface ClientRequestInfoOperations extends org.omg.PortableInterceptor.RequestInfoOperations {
	void add_request_service_context(org.omg.IOP.ServiceContext var0, boolean var1);
	org.omg.IOP.TaggedProfile effective_profile();
	org.omg.CORBA.Object effective_target();
	org.omg.IOP.TaggedComponent get_effective_component(int var0);
	org.omg.IOP.TaggedComponent[] get_effective_components(int var0);
	org.omg.CORBA.Policy get_request_policy(int var0);
	org.omg.CORBA.Any received_exception();
	java.lang.String received_exception_id();
	org.omg.CORBA.Object target();
}

