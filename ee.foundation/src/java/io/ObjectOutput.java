/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public abstract interface ObjectOutput extends java.io.DataOutput {
	public abstract void close() throws java.io.IOException;
	public abstract void flush() throws java.io.IOException;
	public abstract void write(byte[] var0) throws java.io.IOException;
	public abstract void write(byte[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void write(int var0) throws java.io.IOException;
	public abstract void writeObject(java.lang.Object var0) throws java.io.IOException;
}

