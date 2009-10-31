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

package javax.xml.ws.soap;
public interface SOAPBinding extends javax.xml.ws.Binding {
	public final static java.lang.String SOAP11HTTP_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http";
	public final static java.lang.String SOAP11HTTP_MTOM_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";
	public final static java.lang.String SOAP12HTTP_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
	public final static java.lang.String SOAP12HTTP_MTOM_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";
	javax.xml.soap.MessageFactory getMessageFactory();
	java.util.Set<java.lang.String> getRoles();
	javax.xml.soap.SOAPFactory getSOAPFactory();
	boolean isMTOMEnabled();
	void setMTOMEnabled(boolean var0);
	void setRoles(java.util.Set<java.lang.String> var0);
}

