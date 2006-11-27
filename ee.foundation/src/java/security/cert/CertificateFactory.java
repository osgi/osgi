/*
 * $Header$
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
public class CertificateFactory {
	protected CertificateFactory(java.security.cert.CertificateFactorySpi var0, java.security.Provider var1, java.lang.String var2) { }
	public final java.security.cert.CRL generateCRL(java.io.InputStream var0) throws java.security.cert.CRLException { return null; }
	public final java.util.Collection generateCRLs(java.io.InputStream var0) throws java.security.cert.CRLException { return null; }
	public final java.security.cert.CertPath generateCertPath(java.io.InputStream var0) throws java.security.cert.CertificateException { return null; }
	public final java.security.cert.CertPath generateCertPath(java.io.InputStream var0, java.lang.String var1) throws java.security.cert.CertificateException { return null; }
	public final java.security.cert.CertPath generateCertPath(java.util.List var0) throws java.security.cert.CertificateException { return null; }
	public final java.security.cert.Certificate generateCertificate(java.io.InputStream var0) throws java.security.cert.CertificateException { return null; }
	public final java.util.Collection generateCertificates(java.io.InputStream var0) throws java.security.cert.CertificateException { return null; }
	public final java.util.Iterator getCertPathEncodings() { return null; }
	public final static java.security.cert.CertificateFactory getInstance(java.lang.String var0) throws java.security.cert.CertificateException { return null; }
	public final static java.security.cert.CertificateFactory getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchProviderException, java.security.cert.CertificateException { return null; }
	public final static java.security.cert.CertificateFactory getInstance(java.lang.String var0, java.security.Provider var1) throws java.security.cert.CertificateException { return null; }
	public final java.security.Provider getProvider() { return null; }
	public final java.lang.String getType() { return null; }
}

