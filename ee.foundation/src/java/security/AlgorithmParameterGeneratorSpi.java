/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security;
public abstract class AlgorithmParameterGeneratorSpi {
	public AlgorithmParameterGeneratorSpi() { }
	protected abstract java.security.AlgorithmParameters engineGenerateParameters();
	protected abstract void engineInit(int var0, java.security.SecureRandom var1);
	protected abstract void engineInit(java.security.spec.AlgorithmParameterSpec var0, java.security.SecureRandom var1) throws java.security.InvalidAlgorithmParameterException;
}

