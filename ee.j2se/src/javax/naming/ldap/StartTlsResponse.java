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

package javax.naming.ldap;
public abstract class StartTlsResponse implements javax.naming.ldap.ExtendedResponse {
	protected StartTlsResponse() { }
	public abstract void close() throws java.io.IOException;
	public byte[] getEncodedValue() { return null; }
	public java.lang.String getID() { return null; }
	public abstract javax.net.ssl.SSLSession negotiate() throws java.io.IOException;
	public abstract javax.net.ssl.SSLSession negotiate(javax.net.ssl.SSLSocketFactory var0) throws java.io.IOException;
	public abstract void setEnabledCipherSuites(java.lang.String[] var0);
	public abstract void setHostnameVerifier(javax.net.ssl.HostnameVerifier var0);
	public final static java.lang.String OID = "1.3.6.1.4.1.1466.20037";
}

