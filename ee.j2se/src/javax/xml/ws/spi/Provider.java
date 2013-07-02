/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package javax.xml.ws.spi;
public abstract class Provider {
	public final static java.lang.String JAXWSPROVIDER_PROPERTY = "javax.xml.ws.spi.Provider";
	protected Provider() { } 
	public abstract javax.xml.ws.Endpoint createAndPublishEndpoint(java.lang.String var0, java.lang.Object var1);
	public javax.xml.ws.Endpoint createAndPublishEndpoint(java.lang.String var0, java.lang.Object var1, javax.xml.ws.WebServiceFeature... var2) { return null; }
	public javax.xml.ws.Endpoint createEndpoint(java.lang.String var0, java.lang.Class<?> var1, javax.xml.ws.spi.Invoker var2, javax.xml.ws.WebServiceFeature... var3) { return null; }
	public abstract javax.xml.ws.Endpoint createEndpoint(java.lang.String var0, java.lang.Object var1);
	public javax.xml.ws.Endpoint createEndpoint(java.lang.String var0, java.lang.Object var1, javax.xml.ws.WebServiceFeature... var2) { return null; }
	public abstract javax.xml.ws.spi.ServiceDelegate createServiceDelegate(java.net.URL var0, javax.xml.namespace.QName var1, java.lang.Class<? extends javax.xml.ws.Service> var2);
	public javax.xml.ws.spi.ServiceDelegate createServiceDelegate(java.net.URL var0, javax.xml.namespace.QName var1, java.lang.Class<? extends javax.xml.ws.Service> var2, javax.xml.ws.WebServiceFeature... var3) { return null; }
	public abstract javax.xml.ws.wsaddressing.W3CEndpointReference createW3CEndpointReference(java.lang.String var0, javax.xml.namespace.QName var1, javax.xml.namespace.QName var2, java.util.List<org.w3c.dom.Element> var3, java.lang.String var4, java.util.List<org.w3c.dom.Element> var5);
	public javax.xml.ws.wsaddressing.W3CEndpointReference createW3CEndpointReference(java.lang.String var0, javax.xml.namespace.QName var1, javax.xml.namespace.QName var2, javax.xml.namespace.QName var3, java.util.List<org.w3c.dom.Element> var4, java.lang.String var5, java.util.List<org.w3c.dom.Element> var6, java.util.List<org.w3c.dom.Element> var7, java.util.Map<javax.xml.namespace.QName,java.lang.String> var8) { return null; }
	public abstract <T> T getPort(javax.xml.ws.EndpointReference var0, java.lang.Class<T> var1, javax.xml.ws.WebServiceFeature... var2);
	public static javax.xml.ws.spi.Provider provider() { return null; }
	public abstract javax.xml.ws.EndpointReference readEndpointReference(javax.xml.transform.Source var0);
}

