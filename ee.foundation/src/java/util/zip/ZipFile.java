/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.util.zip;
public class ZipFile implements java.util.zip.ZipConstants {
	public ZipFile(java.io.File var0) throws java.util.zip.ZipException, java.io.IOException { }
	public ZipFile(java.io.File var0, int var1) throws java.io.IOException { }
	public ZipFile(java.lang.String var0) throws java.io.IOException { }
	protected void finalize() throws java.io.IOException { }
	public void close() throws java.io.IOException { }
	public java.util.Enumeration entries() { return null; }
	public java.util.zip.ZipEntry getEntry(java.lang.String var0) { return null; }
	public java.io.InputStream getInputStream(java.util.zip.ZipEntry var0) throws java.io.IOException { return null; }
	public java.lang.String getName() { return null; }
	public int size() { return 0; }
	public final static int OPEN_READ = 1;
	public final static int OPEN_DELETE = 4;
}

