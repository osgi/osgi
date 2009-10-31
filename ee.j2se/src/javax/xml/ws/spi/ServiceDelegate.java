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

package javax.xml.ws.spi;
public abstract class ServiceDelegate {
	protected ServiceDelegate() { } 
	public abstract void addPort(javax.xml.namespace.QName var0, java.lang.String var1, java.lang.String var2);
	public abstract <T> javax.xml.ws.Dispatch<T> createDispatch(javax.xml.namespace.QName var0, java.lang.Class<T> var1, javax.xml.ws.Service.Mode var2);
	public abstract <T> javax.xml.ws.Dispatch<T> createDispatch(javax.xml.namespace.QName var0, java.lang.Class<T> var1, javax.xml.ws.Service.Mode var2, javax.xml.ws.WebServiceFeature... var3);
	public abstract javax.xml.ws.Dispatch<java.lang.Object> createDispatch(javax.xml.namespace.QName var0, javax.xml.bind.JAXBContext var1, javax.xml.ws.Service.Mode var2);
	public abstract javax.xml.ws.Dispatch<java.lang.Object> createDispatch(javax.xml.namespace.QName var0, javax.xml.bind.JAXBContext var1, javax.xml.ws.Service.Mode var2, javax.xml.ws.WebServiceFeature... var3);
	public abstract <T> javax.xml.ws.Dispatch<T> createDispatch(javax.xml.ws.EndpointReference var0, java.lang.Class<T> var1, javax.xml.ws.Service.Mode var2, javax.xml.ws.WebServiceFeature... var3);
	public abstract javax.xml.ws.Dispatch<java.lang.Object> createDispatch(javax.xml.ws.EndpointReference var0, javax.xml.bind.JAXBContext var1, javax.xml.ws.Service.Mode var2, javax.xml.ws.WebServiceFeature... var3);
	public abstract java.util.concurrent.Executor getExecutor();
	public abstract javax.xml.ws.handler.HandlerResolver getHandlerResolver();
	public abstract <T> T getPort(java.lang.Class<T> var0);
	public abstract <T> T getPort(java.lang.Class<T> var0, javax.xml.ws.WebServiceFeature... var1);
	public abstract <T> T getPort(javax.xml.namespace.QName var0, java.lang.Class<T> var1);
	public abstract <T> T getPort(javax.xml.namespace.QName var0, java.lang.Class<T> var1, javax.xml.ws.WebServiceFeature... var2);
	public abstract <T> T getPort(javax.xml.ws.EndpointReference var0, java.lang.Class<T> var1, javax.xml.ws.WebServiceFeature... var2);
	public abstract java.util.Iterator<javax.xml.namespace.QName> getPorts();
	public abstract javax.xml.namespace.QName getServiceName();
	public abstract java.net.URL getWSDLDocumentLocation();
	public abstract void setExecutor(java.util.concurrent.Executor var0);
	public abstract void setHandlerResolver(javax.xml.ws.handler.HandlerResolver var0);
}

