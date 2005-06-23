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
public abstract class Signer extends java.security.Identity {
	protected Signer() { }
	public Signer(java.lang.String var0) { }
	public Signer(java.lang.String var0, java.security.IdentityScope var1) throws java.security.KeyManagementException { }
	public java.security.PrivateKey getPrivateKey() { return null; }
	public final void setKeyPair(java.security.KeyPair var0) throws java.security.InvalidParameterException, java.security.KeyException { }
	public java.lang.String toString() { return null; }
}

