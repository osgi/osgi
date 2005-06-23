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
public abstract class KeyPairGenerator extends java.security.KeyPairGeneratorSpi {
	protected KeyPairGenerator(java.lang.String var0) { }
	public final java.security.KeyPair genKeyPair() { return null; }
	public java.lang.String getAlgorithm() { return null; }
	public static java.security.KeyPairGenerator getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
	public static java.security.KeyPairGenerator getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public final java.security.Provider getProvider() { return null; }
	public void initialize(int var0) { }
	public void initialize(int var0, java.security.SecureRandom var1) { }
	public void initialize(java.security.spec.AlgorithmParameterSpec var0) throws java.security.InvalidAlgorithmParameterException { }
	public void initialize(java.security.spec.AlgorithmParameterSpec var0, java.security.SecureRandom var1) throws java.security.InvalidAlgorithmParameterException { }
	public java.security.KeyPair generateKeyPair() { return null; }
}

