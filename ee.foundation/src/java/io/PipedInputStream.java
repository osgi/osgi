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
public class PipedInputStream extends java.io.InputStream {
	public PipedInputStream() { }
	public PipedInputStream(java.io.PipedOutputStream var0) throws java.io.IOException { }
	public int available() throws java.io.IOException { return 0; }
	public void close() throws java.io.IOException { }
	public void connect(java.io.PipedOutputStream var0) throws java.io.IOException { }
	public int read() throws java.io.IOException { return 0; }
	public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
	protected void receive(int var0) throws java.io.IOException { }
	protected byte[] buffer;
	protected int in;
	protected int out;
	protected final static int PIPE_SIZE = 1024;
}

