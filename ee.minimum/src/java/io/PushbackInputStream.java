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

package java.io;
public class PushbackInputStream extends java.io.FilterInputStream {
	public PushbackInputStream(java.io.InputStream var0) { super((java.io.InputStream) null); }
	public PushbackInputStream(java.io.InputStream var0, int var1) { super((java.io.InputStream) null); }
	public int available() throws java.io.IOException { return 0; }
	public void close() throws java.io.IOException { }
	public boolean markSupported() { return false; }
	public int read() throws java.io.IOException { return 0; }
	public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
	public long skip(long var0) throws java.io.IOException { return 0l; }
	public void unread(byte[] var0) throws java.io.IOException { }
	public void unread(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public void unread(int var0) throws java.io.IOException { }
	protected byte[] buf;
	protected int pos;
}

