/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package java.security.cert;
public abstract class CertificateFactorySpi {
	public CertificateFactorySpi() { }
	public abstract java.security.cert.Certificate engineGenerateCertificate(java.io.InputStream var0) throws java.security.cert.CertificateException;
	public abstract java.util.Collection engineGenerateCertificates(java.io.InputStream var0) throws java.security.cert.CertificateException;
	public abstract java.security.cert.CRL engineGenerateCRL(java.io.InputStream var0) throws java.security.cert.CRLException;
	public abstract java.util.Collection engineGenerateCRLs(java.io.InputStream var0) throws java.security.cert.CRLException;
}

