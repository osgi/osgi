/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security;
public abstract class SecureRandomSpi implements java.io.Serializable {
	public SecureRandomSpi() { }
	protected abstract byte[] engineGenerateSeed(int var0);
	protected abstract void engineNextBytes(byte[] var0);
	protected abstract void engineSetSeed(byte[] var0);
}

