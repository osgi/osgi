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

package java.security;
public abstract class KeyStoreSpi {
	public KeyStoreSpi() { }
	public abstract java.util.Enumeration engineAliases();
	public abstract boolean engineContainsAlias(java.lang.String var0);
	public abstract void engineDeleteEntry(java.lang.String var0) throws java.security.KeyStoreException;
	public abstract java.security.cert.Certificate engineGetCertificate(java.lang.String var0);
	public abstract java.lang.String engineGetCertificateAlias(java.security.cert.Certificate var0);
	public abstract java.security.cert.Certificate[] engineGetCertificateChain(java.lang.String var0);
	public abstract java.util.Date engineGetCreationDate(java.lang.String var0);
	public abstract java.security.Key engineGetKey(java.lang.String var0, char[] var1) throws java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException;
	public abstract boolean engineIsCertificateEntry(java.lang.String var0);
	public abstract boolean engineIsKeyEntry(java.lang.String var0);
	public abstract void engineLoad(java.io.InputStream var0, char[] var1) throws java.io.IOException, java.security.NoSuchAlgorithmException, java.security.cert.CertificateException;
	public abstract void engineSetCertificateEntry(java.lang.String var0, java.security.cert.Certificate var1) throws java.security.KeyStoreException;
	public abstract void engineSetKeyEntry(java.lang.String var0, byte[] var1, java.security.cert.Certificate[] var2) throws java.security.KeyStoreException;
	public abstract void engineSetKeyEntry(java.lang.String var0, java.security.Key var1, char[] var2, java.security.cert.Certificate[] var3) throws java.security.KeyStoreException;
	public abstract int engineSize();
	public abstract void engineStore(java.io.OutputStream var0, char[] var1) throws java.io.IOException, java.security.NoSuchAlgorithmException, java.security.cert.CertificateException;
}

