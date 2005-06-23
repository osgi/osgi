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
public class PushbackReader extends java.io.FilterReader {
	public PushbackReader(java.io.Reader var0) { super((java.io.Reader) null); }
	public PushbackReader(java.io.Reader var0, int var1) { super((java.io.Reader) null); }
	public void close() throws java.io.IOException { }
	public void mark(int var0) throws java.io.IOException { }
	public boolean markSupported() { return false; }
	public int read() throws java.io.IOException { return 0; }
	public int read(char[] var0, int var1, int var2) throws java.io.IOException { return 0; }
	public boolean ready() throws java.io.IOException { return false; }
	public void reset() throws java.io.IOException { }
	public void unread(char[] var0) throws java.io.IOException { }
	public void unread(char[] var0, int var1, int var2) throws java.io.IOException { }
	public void unread(int var0) throws java.io.IOException { }
}

