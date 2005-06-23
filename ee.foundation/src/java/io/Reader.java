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
public abstract class Reader {
	protected Reader() { }
	protected Reader(java.lang.Object var0) { }
	public abstract void close() throws java.io.IOException;
	public void mark(int var0) throws java.io.IOException { }
	public boolean markSupported() { return false; }
	public int read() throws java.io.IOException { return 0; }
	public int read(char[] var0) throws java.io.IOException { return 0; }
	public abstract int read(char[] var0, int var1, int var2) throws java.io.IOException;
	public boolean ready() throws java.io.IOException { return false; }
	public void reset() throws java.io.IOException { }
	public long skip(long var0) throws java.io.IOException { return 0l; }
	protected java.lang.Object lock;
}

