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

package javax.xml.ws.spi.http;
public abstract class HttpExchange {
	public final static java.lang.String REQUEST_CIPHER_SUITE = "javax.xml.ws.spi.http.request.cipher.suite";
	public final static java.lang.String REQUEST_KEY_SIZE = "javax.xml.ws.spi.http.request.key.size";
	public final static java.lang.String REQUEST_X509CERTIFICATE = "javax.xml.ws.spi.http.request.cert.X509Certificate";
	public HttpExchange() { } 
	public abstract void addResponseHeader(java.lang.String var0, java.lang.String var1);
	public abstract void close() throws java.io.IOException;
	public abstract java.lang.Object getAttribute(java.lang.String var0);
	public abstract java.util.Set<java.lang.String> getAttributeNames();
	public abstract java.lang.String getContextPath();
	public abstract javax.xml.ws.spi.http.HttpContext getHttpContext();
	public abstract java.net.InetSocketAddress getLocalAddress();
	public abstract java.lang.String getPathInfo();
	public abstract java.lang.String getProtocol();
	public abstract java.lang.String getQueryString();
	public abstract java.net.InetSocketAddress getRemoteAddress();
	public abstract java.io.InputStream getRequestBody() throws java.io.IOException;
	public abstract java.lang.String getRequestHeader(java.lang.String var0);
	public abstract java.util.Map<java.lang.String,java.util.List<java.lang.String>> getRequestHeaders();
	public abstract java.lang.String getRequestMethod();
	public abstract java.lang.String getRequestURI();
	public abstract java.io.OutputStream getResponseBody() throws java.io.IOException;
	public abstract java.util.Map<java.lang.String,java.util.List<java.lang.String>> getResponseHeaders();
	public abstract java.lang.String getScheme();
	public abstract java.security.Principal getUserPrincipal();
	public abstract boolean isUserInRole(java.lang.String var0);
	public abstract void setStatus(int var0);
}

