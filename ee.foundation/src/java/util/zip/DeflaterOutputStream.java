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

package java.util.zip;
public class DeflaterOutputStream extends java.io.FilterOutputStream {
	public DeflaterOutputStream(java.io.OutputStream var0, java.util.zip.Deflater var1) { super((java.io.OutputStream) null); }
	public DeflaterOutputStream(java.io.OutputStream var0) { super((java.io.OutputStream) null); }
	public DeflaterOutputStream(java.io.OutputStream var0, java.util.zip.Deflater var1, int var2) { super((java.io.OutputStream) null); }
	protected void deflate() throws java.io.IOException { }
	public void close() throws java.io.IOException { }
	public void finish() throws java.io.IOException { }
	public void write(int var0) throws java.io.IOException { }
	public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
	protected byte[] buf;
	protected java.util.zip.Deflater def;
}

