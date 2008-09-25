/*
 * $Revision$
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
public class HandshakeCompletedEvent extends java.util.EventObject {
	public HandshakeCompletedEvent(javax.net.ssl.SSLSocket var0, javax.net.ssl.SSLSession var1) { super((java.lang.Object) null); }
	public java.lang.String getCipherSuite() { return null; }
	public java.security.cert.Certificate[] getLocalCertificates() { return null; }
	public javax.security.cert.X509Certificate[] getPeerCertificateChain() throws javax.net.ssl.SSLPeerUnverifiedException { return null; }
	public java.security.cert.Certificate[] getPeerCertificates() throws javax.net.ssl.SSLPeerUnverifiedException { return null; }
	public javax.net.ssl.SSLSession getSession() { return null; }
	public javax.net.ssl.SSLSocket getSocket() { return null; }
}

