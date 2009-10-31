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
public abstract class Endpoint {
	public final static java.lang.String WSDL_PORT = "javax.xml.ws.wsdl.port";
	public final static java.lang.String WSDL_SERVICE = "javax.xml.ws.wsdl.service";
	public Endpoint() { } 
	public static javax.xml.ws.Endpoint create(java.lang.Object var0) { return null; }
	public static javax.xml.ws.Endpoint create(java.lang.String var0, java.lang.Object var1) { return null; }
	public abstract javax.xml.ws.Binding getBinding();
	public abstract <T extends javax.xml.ws.EndpointReference> T getEndpointReference(java.lang.Class<T> var0, org.w3c.dom.Element... var1);
	public abstract javax.xml.ws.EndpointReference getEndpointReference(org.w3c.dom.Element... var0);
	public abstract java.util.concurrent.Executor getExecutor();
	public abstract java.lang.Object getImplementor();
	public abstract java.util.List<javax.xml.transform.Source> getMetadata();
	public abstract java.util.Map<java.lang.String,java.lang.Object> getProperties();
	public abstract boolean isPublished();
	public abstract void publish(java.lang.Object var0);
	public abstract void publish(java.lang.String var0);
	public static javax.xml.ws.Endpoint publish(java.lang.String var0, java.lang.Object var1) { return null; }
	public abstract void setExecutor(java.util.concurrent.Executor var0);
	public abstract void setMetadata(java.util.List<javax.xml.transform.Source> var0);
	public abstract void setProperties(java.util.Map<java.lang.String,java.lang.Object> var0);
	public abstract void stop();
}

