/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security;
public class DigestOutputStream extends java.io.FilterOutputStream {
	public DigestOutputStream(java.io.OutputStream var0, java.security.MessageDigest var1) { super((java.io.OutputStream) null); }
	public java.security.MessageDigest getMessageDigest() { return null; }
	public void on(boolean var0) { }
	public void setMessageDigest(java.security.MessageDigest var0) { }
	public java.lang.String toString() { return null; }
	public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public void write(int var0) throws java.io.IOException { }
	protected java.security.MessageDigest digest;
}

