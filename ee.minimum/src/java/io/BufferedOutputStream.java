/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public class BufferedOutputStream extends java.io.FilterOutputStream {
	public BufferedOutputStream(java.io.OutputStream var0) { super((java.io.OutputStream) null); }
	public BufferedOutputStream(java.io.OutputStream var0, int var1) { super((java.io.OutputStream) null); }
	public void flush() throws java.io.IOException { }
	public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public void write(int var0) throws java.io.IOException { }
	protected byte[] buf;
	protected int count;
}

