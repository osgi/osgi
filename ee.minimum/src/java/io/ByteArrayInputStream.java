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
public class ByteArrayInputStream extends java.io.InputStream {
	public ByteArrayInputStream(byte[] var0) { }
	public ByteArrayInputStream(byte[] var0, int var1, int var2) { }
	public int available() { return 0; }
	public void close() throws java.io.IOException { }
	public void mark(int var0) { }
	public boolean markSupported() { return false; }
	public int read() { return 0; }
	public int read(byte[] var0, int var1, int var2) { return 0; }
	public void reset() { }
	public long skip(long var0) { return 0l; }
	protected byte[] buf;
	protected int pos;
	protected int mark;
	protected int count;
}

