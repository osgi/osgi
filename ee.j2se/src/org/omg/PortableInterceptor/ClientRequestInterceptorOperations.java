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
public interface ClientRequestInterceptorOperations extends org.omg.PortableInterceptor.InterceptorOperations {
	void receive_exception(org.omg.PortableInterceptor.ClientRequestInfo var0) throws org.omg.PortableInterceptor.ForwardRequest;
	void receive_other(org.omg.PortableInterceptor.ClientRequestInfo var0) throws org.omg.PortableInterceptor.ForwardRequest;
	void receive_reply(org.omg.PortableInterceptor.ClientRequestInfo var0);
	void send_poll(org.omg.PortableInterceptor.ClientRequestInfo var0);
	void send_request(org.omg.PortableInterceptor.ClientRequestInfo var0) throws org.omg.PortableInterceptor.ForwardRequest;
}

