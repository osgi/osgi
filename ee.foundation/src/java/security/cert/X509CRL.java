/*
 * $Date$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2006). All Rights Reserved.
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
public abstract class X509CRL extends java.security.cert.CRL implements java.security.cert.X509Extension {
	protected X509CRL() { super((java.lang.String) null); }
	public abstract byte[] getEncoded() throws java.security.cert.CRLException;
	public abstract java.security.Principal getIssuerDN();
	public javax.security.auth.x500.X500Principal getIssuerX500Principal() { return null; }
	public abstract java.util.Date getNextUpdate();
	public abstract java.security.cert.X509CRLEntry getRevokedCertificate(java.math.BigInteger var0);
	public abstract java.util.Set getRevokedCertificates();
	public abstract java.lang.String getSigAlgName();
	public abstract java.lang.String getSigAlgOID();
	public abstract byte[] getSigAlgParams();
	public abstract byte[] getSignature();
	public abstract byte[] getTBSCertList() throws java.security.cert.CRLException;
	public abstract java.util.Date getThisUpdate();
	public abstract int getVersion();
	public abstract void verify(java.security.PublicKey var0) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.SignatureException, java.security.cert.CRLException;
	public abstract void verify(java.security.PublicKey var0, java.lang.String var1) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException, java.security.SignatureException, java.security.cert.CRLException;
}

