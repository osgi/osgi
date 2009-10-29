/*
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package javax.net.ssl;
public abstract class HttpsURLConnection extends java.net.HttpURLConnection {
	protected HttpsURLConnection(java.net.URL var0) throws java.io.IOException { super((java.net.URL) null); }
	public abstract java.lang.String getCipherSuite();
	public static javax.net.ssl.HostnameVerifier getDefaultHostnameVerifier() { return null; }
	public static javax.net.ssl.SSLSocketFactory getDefaultSSLSocketFactory() { return null; }
	public javax.net.ssl.HostnameVerifier getHostnameVerifier() { return null; }
	public abstract java.security.cert.Certificate[] getLocalCertificates();
	public javax.net.ssl.SSLSocketFactory getSSLSocketFactory() { return null; }
	public abstract java.security.cert.Certificate[] getServerCertificates() throws javax.net.ssl.SSLPeerUnverifiedException;
	public static void setDefaultHostnameVerifier(javax.net.ssl.HostnameVerifier var0) { }
	public static void setDefaultSSLSocketFactory(javax.net.ssl.SSLSocketFactory var0) { }
	public void setHostnameVerifier(javax.net.ssl.HostnameVerifier var0) { }
	public void setSSLSocketFactory(javax.net.ssl.SSLSocketFactory var0) { }
	protected javax.net.ssl.HostnameVerifier hostnameVerifier;
}

