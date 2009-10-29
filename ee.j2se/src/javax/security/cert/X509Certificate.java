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

package javax.security.cert;
public abstract class X509Certificate extends javax.security.cert.Certificate {
	public X509Certificate() { }
	public abstract void checkValidity() throws javax.security.cert.CertificateExpiredException, javax.security.cert.CertificateNotYetValidException;
	public abstract void checkValidity(java.util.Date var0) throws javax.security.cert.CertificateExpiredException, javax.security.cert.CertificateNotYetValidException;
	public final static javax.security.cert.X509Certificate getInstance(java.io.InputStream var0) throws javax.security.cert.CertificateException { return null; }
	public final static javax.security.cert.X509Certificate getInstance(byte[] var0) throws javax.security.cert.CertificateException { return null; }
	public abstract java.security.Principal getIssuerDN();
	public abstract java.util.Date getNotAfter();
	public abstract java.util.Date getNotBefore();
	public abstract java.math.BigInteger getSerialNumber();
	public abstract java.lang.String getSigAlgName();
	public abstract java.lang.String getSigAlgOID();
	public abstract byte[] getSigAlgParams();
	public abstract java.security.Principal getSubjectDN();
	public abstract int getVersion();
}

