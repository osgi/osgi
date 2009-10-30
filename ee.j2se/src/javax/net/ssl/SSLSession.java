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

package javax.net.ssl;
public interface SSLSession {
	int getApplicationBufferSize();
	java.lang.String getCipherSuite();
	long getCreationTime();
	byte[] getId();
	long getLastAccessedTime();
	java.security.cert.Certificate[] getLocalCertificates();
	java.security.Principal getLocalPrincipal();
	int getPacketBufferSize();
	javax.security.cert.X509Certificate[] getPeerCertificateChain() throws javax.net.ssl.SSLPeerUnverifiedException;
	java.security.cert.Certificate[] getPeerCertificates() throws javax.net.ssl.SSLPeerUnverifiedException;
	java.lang.String getPeerHost();
	int getPeerPort();
	java.security.Principal getPeerPrincipal() throws javax.net.ssl.SSLPeerUnverifiedException;
	java.lang.String getProtocol();
	javax.net.ssl.SSLSessionContext getSessionContext();
	java.lang.Object getValue(java.lang.String var0);
	java.lang.String[] getValueNames();
	void invalidate();
	boolean isValid();
	void putValue(java.lang.String var0, java.lang.Object var1);
	void removeValue(java.lang.String var0);
}

