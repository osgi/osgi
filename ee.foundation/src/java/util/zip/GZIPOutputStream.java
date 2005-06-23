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
public class GZIPOutputStream extends java.util.zip.DeflaterOutputStream {
	public GZIPOutputStream(java.io.OutputStream var0) throws java.io.IOException { super((java.io.OutputStream) null, (java.util.zip.Deflater) null, 0); }
	public GZIPOutputStream(java.io.OutputStream var0, int var1) throws java.io.IOException { super((java.io.OutputStream) null, (java.util.zip.Deflater) null, 0); }
	public void finish() throws java.io.IOException { }
	public void close() throws java.io.IOException { }
	public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
	protected java.util.zip.CRC32 crc;
}

