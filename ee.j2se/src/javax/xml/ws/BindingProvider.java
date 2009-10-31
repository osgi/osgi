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

package javax.xml.ws;
public interface BindingProvider {
	public final static java.lang.String ENDPOINT_ADDRESS_PROPERTY = "javax.xml.ws.service.endpoint.address";
	public final static java.lang.String PASSWORD_PROPERTY = "javax.xml.ws.security.auth.password";
	public final static java.lang.String SESSION_MAINTAIN_PROPERTY = "javax.xml.ws.session.maintain";
	public final static java.lang.String SOAPACTION_URI_PROPERTY = "javax.xml.ws.soap.http.soapaction.uri";
	public final static java.lang.String SOAPACTION_USE_PROPERTY = "javax.xml.ws.soap.http.soapaction.use";
	public final static java.lang.String USERNAME_PROPERTY = "javax.xml.ws.security.auth.username";
	javax.xml.ws.Binding getBinding();
	javax.xml.ws.EndpointReference getEndpointReference();
	<T extends javax.xml.ws.EndpointReference> T getEndpointReference(java.lang.Class<T> var0);
	java.util.Map<java.lang.String,java.lang.Object> getRequestContext();
	java.util.Map<java.lang.String,java.lang.Object> getResponseContext();
}

