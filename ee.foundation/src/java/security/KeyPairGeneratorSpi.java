/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security;
public abstract class KeyPairGeneratorSpi {
	public KeyPairGeneratorSpi() { }
	public abstract java.security.KeyPair generateKeyPair();
	public abstract void initialize(int var0, java.security.SecureRandom var1);
	public void initialize(java.security.spec.AlgorithmParameterSpec var0, java.security.SecureRandom var1) throws java.security.InvalidAlgorithmParameterException { }
}

