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
public abstract class KeyPairGeneratorSpi {
	public KeyPairGeneratorSpi() { }
	public abstract java.security.KeyPair generateKeyPair();
	public abstract void initialize(int var0, java.security.SecureRandom var1);
	public void initialize(java.security.spec.AlgorithmParameterSpec var0, java.security.SecureRandom var1) throws java.security.InvalidAlgorithmParameterException { }
}

