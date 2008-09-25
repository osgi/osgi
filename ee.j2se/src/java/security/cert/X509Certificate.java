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

package java.security.cert;
public abstract class X509Certificate extends java.security.cert.Certificate implements java.security.cert.X509Extension {
	protected X509Certificate() { super((java.lang.String) null); }
	public abstract void checkValidity() throws java.security.cert.CertificateExpiredException, java.security.cert.CertificateNotYetValidException;
	public abstract void checkValidity(java.util.Date var0) throws java.security.cert.CertificateExpiredException, java.security.cert.CertificateNotYetValidException;
	public abstract int getBasicConstraints();
	public java.util.List getExtendedKeyUsage() throws java.security.cert.CertificateParsingException { return null; }
	public java.util.Collection getIssuerAlternativeNames() throws java.security.cert.CertificateParsingException { return null; }
	public abstract java.security.Principal getIssuerDN();
	public abstract boolean[] getIssuerUniqueID();
	public javax.security.auth.x500.X500Principal getIssuerX500Principal() { return null; }
	public abstract boolean[] getKeyUsage();
	public abstract java.util.Date getNotAfter();
	public abstract java.util.Date getNotBefore();
	public abstract java.math.BigInteger getSerialNumber();
	public abstract java.lang.String getSigAlgName();
	public abstract java.lang.String getSigAlgOID();
	public abstract byte[] getSigAlgParams();
	public abstract byte[] getSignature();
	public java.util.Collection getSubjectAlternativeNames() throws java.security.cert.CertificateParsingException { return null; }
	public abstract java.security.Principal getSubjectDN();
	public abstract boolean[] getSubjectUniqueID();
	public javax.security.auth.x500.X500Principal getSubjectX500Principal() { return null; }
	public abstract byte[] getTBSCertificate() throws java.security.cert.CertificateEncodingException;
	public abstract int getVersion();
}

