/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public abstract class Writer {
	protected Writer() { }
	protected Writer(java.lang.Object var0) { }
	public abstract void close() throws java.io.IOException;
	public abstract void flush() throws java.io.IOException;
	public void write(char[] var0) throws java.io.IOException { }
	public abstract void write(char[] var0, int var1, int var2) throws java.io.IOException;
	public void write(int var0) throws java.io.IOException { }
	public void write(java.lang.String var0) throws java.io.IOException { }
	public void write(java.lang.String var0, int var1, int var2) throws java.io.IOException { }
	protected java.lang.Object lock;
}

