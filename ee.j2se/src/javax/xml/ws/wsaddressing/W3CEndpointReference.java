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

package javax.xml.ws.wsaddressing;
@javax.xml.bind.annotation.XmlRootElement(name="EndpointReference",namespace="http://www.w3.org/2005/08/addressing")
@javax.xml.bind.annotation.XmlType(name="EndpointReferenceType",namespace="http://www.w3.org/2005/08/addressing")
public final class W3CEndpointReference extends javax.xml.ws.EndpointReference {
	protected final static java.lang.String NS = "http://www.w3.org/2005/08/addressing";
	protected W3CEndpointReference() { } 
	public W3CEndpointReference(javax.xml.transform.Source var0) { } 
	public void writeTo(javax.xml.transform.Result var0) { }
}

