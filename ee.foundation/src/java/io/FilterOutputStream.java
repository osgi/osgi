/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public class FilterOutputStream extends java.io.OutputStream {
	public FilterOutputStream(java.io.OutputStream var0) { }
	public void close() throws java.io.IOException { }
	public void flush() throws java.io.IOException { }
	public void write(byte[] var0) throws java.io.IOException { }
	public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public void write(int var0) throws java.io.IOException { }
	protected java.io.OutputStream out;
}

