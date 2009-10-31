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

package javax.xml.ws.handler;
public interface MessageContext extends java.util.Map<java.lang.String,java.lang.Object> {
	public enum Scope {
		APPLICATION,
		HANDLER;
	}
	public final static java.lang.String HTTP_REQUEST_HEADERS = "javax.xml.ws.http.request.headers";
	public final static java.lang.String HTTP_REQUEST_METHOD = "javax.xml.ws.http.request.method";
	public final static java.lang.String HTTP_RESPONSE_CODE = "javax.xml.ws.http.response.code";
	public final static java.lang.String HTTP_RESPONSE_HEADERS = "javax.xml.ws.http.response.headers";
	public final static java.lang.String INBOUND_MESSAGE_ATTACHMENTS = "javax.xml.ws.binding.attachments.inbound";
	public final static java.lang.String MESSAGE_OUTBOUND_PROPERTY = "javax.xml.ws.handler.message.outbound";
	public final static java.lang.String OUTBOUND_MESSAGE_ATTACHMENTS = "javax.xml.ws.binding.attachments.outbound";
	public final static java.lang.String PATH_INFO = "javax.xml.ws.http.request.pathinfo";
	public final static java.lang.String QUERY_STRING = "javax.xml.ws.http.request.querystring";
	public final static java.lang.String REFERENCE_PARAMETERS = "javax.xml.ws.reference.parameters";
	public final static java.lang.String SERVLET_CONTEXT = "javax.xml.ws.servlet.context";
	public final static java.lang.String SERVLET_REQUEST = "javax.xml.ws.servlet.request";
	public final static java.lang.String SERVLET_RESPONSE = "javax.xml.ws.servlet.response";
	public final static java.lang.String WSDL_DESCRIPTION = "javax.xml.ws.wsdl.description";
	public final static java.lang.String WSDL_INTERFACE = "javax.xml.ws.wsdl.interface";
	public final static java.lang.String WSDL_OPERATION = "javax.xml.ws.wsdl.operation";
	public final static java.lang.String WSDL_PORT = "javax.xml.ws.wsdl.port";
	public final static java.lang.String WSDL_SERVICE = "javax.xml.ws.wsdl.service";
	javax.xml.ws.handler.MessageContext.Scope getScope(java.lang.String var0);
	void setScope(java.lang.String var0, javax.xml.ws.handler.MessageContext.Scope var1);
}

