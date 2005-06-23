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
public class DigestInputStream extends java.io.FilterInputStream {
	public DigestInputStream(java.io.InputStream var0, java.security.MessageDigest var1) { super((java.io.InputStream) null); }
	public java.security.MessageDigest getMessageDigest() { return null; }
	public void on(boolean var0) { }
	public int read() throws java.io.IOException { return 0; }
	public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
	public void setMessageDigest(java.security.MessageDigest var0) { }
	public java.lang.String toString() { return null; }
	protected java.security.MessageDigest digest;
}

