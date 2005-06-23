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
public class FileOutputStream extends java.io.OutputStream {
	public FileOutputStream(java.io.File var0) throws java.io.FileNotFoundException { }
	public FileOutputStream(java.io.FileDescriptor var0) { }
	public FileOutputStream(java.lang.String var0) throws java.io.FileNotFoundException { }
	public FileOutputStream(java.lang.String var0, boolean var1) throws java.io.FileNotFoundException { }
	public void close() throws java.io.IOException { }
	protected void finalize() throws java.io.IOException { }
	public final java.io.FileDescriptor getFD() throws java.io.IOException { return null; }
	public void write(byte[] var0) throws java.io.IOException { }
	public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public void write(int var0) throws java.io.IOException { }
}

