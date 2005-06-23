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
public class SecureRandom extends java.util.Random {
	public SecureRandom() { }
	public SecureRandom(byte[] var0) { }
	protected SecureRandom(java.security.SecureRandomSpi var0, java.security.Provider var1) { }
	public byte[] generateSeed(int var0) { return null; }
	public static java.security.SecureRandom getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
	public static java.security.SecureRandom getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
	public final java.security.Provider getProvider() { return null; }
	public static byte[] getSeed(int var0) { return null; }
	protected final int next(int var0) { return 0; }
	public void nextBytes(byte[] var0) { }
	public void setSeed(byte[] var0) { }
	public void setSeed(long var0) { }
}

