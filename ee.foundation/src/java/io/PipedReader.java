/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public class PipedReader extends java.io.Reader {
	public PipedReader() { }
	public PipedReader(java.io.PipedWriter var0) throws java.io.IOException { }
	public void close() throws java.io.IOException { }
	public void connect(java.io.PipedWriter var0) throws java.io.IOException { }
	public int read() throws java.io.IOException { return 0; }
	public int read(char[] var0, int var1, int var2) throws java.io.IOException { return 0; }
	public boolean ready() throws java.io.IOException { return false; }
}

