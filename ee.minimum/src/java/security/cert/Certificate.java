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
public abstract class Certificate implements java.io.Serializable {
	protected Certificate(java.lang.String var0) { }
	public boolean equals(java.lang.Object var0) { return false; }
	public abstract byte[] getEncoded() throws java.security.cert.CertificateEncodingException;
	public abstract java.security.PublicKey getPublicKey();
	public final java.lang.String getType() { return null; }
	public int hashCode() { return 0; }
	public abstract java.lang.String toString();
	public abstract void verify(java.security.PublicKey var0) throws java.security.cert.CertificateException, java.security.NoSuchAlgorithmException, java.security.InvalidKeyException, java.security.NoSuchProviderException, java.security.SignatureException;
	public abstract void verify(java.security.PublicKey var0, java.lang.String var1) throws java.security.cert.CertificateException, java.security.NoSuchAlgorithmException, java.security.InvalidKeyException, java.security.NoSuchProviderException, java.security.SignatureException;
	protected java.lang.Object writeReplace() throws java.io.ObjectStreamException { return null; }
	protected static class CertificateRep implements java.io.Serializable {
		protected CertificateRep(java.lang.String var0, byte[] var1) { }
		protected java.lang.Object readResolve() throws java.io.ObjectStreamException { return null; }
	}
}

