/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public abstract class OutputStream {
	public OutputStream() { }
	public void close() throws java.io.IOException { }
	public void flush() throws java.io.IOException { }
	public void write(byte[] var0) throws java.io.IOException { }
	public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public abstract void write(int var0) throws java.io.IOException;
}

