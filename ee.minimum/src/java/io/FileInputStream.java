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
public class FileInputStream extends java.io.InputStream {
	public FileInputStream(java.io.File var0) throws java.io.FileNotFoundException { }
	public FileInputStream(java.io.FileDescriptor var0) { }
	public FileInputStream(java.lang.String var0) throws java.io.FileNotFoundException { }
	public int available() throws java.io.IOException { return 0; }
	public void close() throws java.io.IOException { }
	protected void finalize() throws java.io.IOException { }
	public final java.io.FileDescriptor getFD() throws java.io.IOException { return null; }
	public int read() throws java.io.IOException { return 0; }
	public int read(byte[] var0) throws java.io.IOException { return 0; }
	public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
	public long skip(long var0) throws java.io.IOException { return 0l; }
}

