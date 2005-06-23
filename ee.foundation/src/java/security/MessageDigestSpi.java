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
public abstract class MessageDigestSpi {
	public MessageDigestSpi() { }
	public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
	protected abstract byte[] engineDigest();
	protected int engineDigest(byte[] var0, int var1, int var2) throws java.security.DigestException { return 0; }
	protected int engineGetDigestLength() { return 0; }
	protected abstract void engineReset();
	protected abstract void engineUpdate(byte[] var0, int var1, int var2);
	protected abstract void engineUpdate(byte var0);
}

