/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.util.zip;
public class ZipOutputStream extends java.util.zip.DeflaterOutputStream implements java.util.zip.ZipConstants {
	public ZipOutputStream(java.io.OutputStream var0) { super((java.io.OutputStream) null, (java.util.zip.Deflater) null, 0); }
	public void close() throws java.io.IOException { }
	public void closeEntry() throws java.io.IOException { }
	public void finish() throws java.io.IOException { }
	public void putNextEntry(java.util.zip.ZipEntry var0) throws java.io.IOException { }
	public void setComment(java.lang.String var0) { }
	public void setLevel(int var0) { }
	public void setMethod(int var0) { }
	public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public final static int DEFLATED = 8;
	public final static int STORED = 0;
}

