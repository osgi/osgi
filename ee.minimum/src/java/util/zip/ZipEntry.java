/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.util.zip;
public class ZipEntry implements java.util.zip.ZipConstants, java.lang.Cloneable {
	public ZipEntry(java.lang.String var0) { }
	public java.lang.String getComment() { return null; }
	public long getCompressedSize() { return 0l; }
	public long getCrc() { return 0l; }
	public byte[] getExtra() { return null; }
	public int getMethod() { return 0; }
	public java.lang.String getName() { return null; }
	public long getSize() { return 0l; }
	public long getTime() { return 0l; }
	public boolean isDirectory() { return false; }
	public void setComment(java.lang.String var0) { }
	public void setCompressedSize(long var0) { }
	public void setCrc(long var0) { }
	public void setExtra(byte[] var0) { }
	public void setMethod(int var0) { }
	public void setSize(long var0) { }
	public void setTime(long var0) { }
	public java.lang.String toString() { return null; }
	public ZipEntry(java.util.zip.ZipEntry var0) { }
	public java.lang.Object clone() { return null; }
	public int hashCode() { return 0; }
	public final static int DEFLATED = 8;
	public final static int STORED = 0;
}

