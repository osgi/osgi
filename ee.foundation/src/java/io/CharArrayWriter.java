/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public class CharArrayWriter extends java.io.Writer {
	public CharArrayWriter() { }
	public CharArrayWriter(int var0) { }
	public void close() { }
	public void flush() { }
	public void reset() { }
	public int size() { return 0; }
	public char[] toCharArray() { return null; }
	public java.lang.String toString() { return null; }
	public void write(char[] var0, int var1, int var2) { }
	public void write(int var0) { }
	public void write(java.lang.String var0, int var1, int var2) { }
	public void writeTo(java.io.Writer var0) throws java.io.IOException { }
	protected char[] buf;
	protected int count;
}

