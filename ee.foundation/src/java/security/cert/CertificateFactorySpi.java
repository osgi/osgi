/*
 * $Id$
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
public abstract class CertificateFactorySpi {
	public CertificateFactorySpi() { }
	public abstract java.security.cert.CRL engineGenerateCRL(java.io.InputStream var0) throws java.security.cert.CRLException;
	public abstract java.util.Collection engineGenerateCRLs(java.io.InputStream var0) throws java.security.cert.CRLException;
	public java.security.cert.CertPath engineGenerateCertPath(java.io.InputStream var0) throws java.security.cert.CertificateException { return null; }
	public java.security.cert.CertPath engineGenerateCertPath(java.io.InputStream var0, java.lang.String var1) throws java.security.cert.CertificateException { return null; }
	public java.security.cert.CertPath engineGenerateCertPath(java.util.List var0) throws java.security.cert.CertificateException { return null; }
	public abstract java.security.cert.Certificate engineGenerateCertificate(java.io.InputStream var0) throws java.security.cert.CertificateException;
	public abstract java.util.Collection engineGenerateCertificates(java.io.InputStream var0) throws java.security.cert.CertificateException;
	public java.util.Iterator engineGetCertPathEncodings() { return null; }
}

