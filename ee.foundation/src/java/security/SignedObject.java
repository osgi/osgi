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
public final class SignedObject implements java.io.Serializable {
	public SignedObject(java.io.Serializable var0, java.security.PrivateKey var1, java.security.Signature var2) throws java.io.IOException, java.security.InvalidKeyException, java.security.SignatureException { }
	public java.lang.String getAlgorithm() { return null; }
	public byte[] getSignature() { return null; }
	public boolean verify(java.security.PublicKey var0, java.security.Signature var1) throws java.security.InvalidKeyException, java.security.SignatureException { return false; }
	public java.lang.Object getObject() throws java.io.IOException, java.lang.ClassNotFoundException { return null; }
}

