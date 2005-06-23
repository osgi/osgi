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
public class AlgorithmParameterGenerator {
	protected AlgorithmParameterGenerator(java.security.AlgorithmParameterGeneratorSpi var0, java.security.Provider var1, java.lang.String var2) { }
	public final java.security.AlgorithmParameters generateParameters() { return null; }
	public final java.lang.String getAlgorithm() { return null; }
	public static java.security.AlgorithmParameterGenerator getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
	public static java.security.AlgorithmParameterGenerator getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public final java.security.Provider getProvider() { return null; }
	public final void init(int var0) { }
	public final void init(int var0, java.security.SecureRandom var1) { }
	public final void init(java.security.spec.AlgorithmParameterSpec var0) throws java.security.InvalidAlgorithmParameterException { }
	public final void init(java.security.spec.AlgorithmParameterSpec var0, java.security.SecureRandom var1) throws java.security.InvalidAlgorithmParameterException { }
}

