/*
 * $Date$
 *
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
public abstract interface SSLSession {
	public abstract java.lang.String getCipherSuite();
	public abstract long getCreationTime();
	public abstract byte[] getId();
	public abstract long getLastAccessedTime();
	public abstract java.security.cert.Certificate[] getLocalCertificates();
	public abstract javax.security.cert.X509Certificate[] getPeerCertificateChain() throws javax.net.ssl.SSLPeerUnverifiedException;
	public abstract java.security.cert.Certificate[] getPeerCertificates() throws javax.net.ssl.SSLPeerUnverifiedException;
	public abstract java.lang.String getPeerHost();
	public abstract java.lang.String getProtocol();
	public abstract javax.net.ssl.SSLSessionContext getSessionContext();
	public abstract java.lang.Object getValue(java.lang.String var0);
	public abstract java.lang.String[] getValueNames();
	public abstract void invalidate();
	public abstract void putValue(java.lang.String var0, java.lang.Object var1);
	public abstract void removeValue(java.lang.String var0);
}

