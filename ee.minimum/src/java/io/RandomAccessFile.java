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
public class RandomAccessFile implements java.io.DataInput, java.io.DataOutput {
	public RandomAccessFile(java.io.File var0, java.lang.String var1) throws java.io.FileNotFoundException { }
	public RandomAccessFile(java.lang.String var0, java.lang.String var1) throws java.io.FileNotFoundException { }
	public void close() throws java.io.IOException { }
	public final java.io.FileDescriptor getFD() throws java.io.IOException { return null; }
	public long getFilePointer() throws java.io.IOException { return 0l; }
	public long length() throws java.io.IOException { return 0l; }
	public int read() throws java.io.IOException { return 0; }
	public int read(byte[] var0) throws java.io.IOException { return 0; }
	public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
	public final boolean readBoolean() throws java.io.IOException { return false; }
	public final byte readByte() throws java.io.IOException { return 0; }
	public final char readChar() throws java.io.IOException { return 0; }
	public final double readDouble() throws java.io.IOException { return 0.0d; }
	public final float readFloat() throws java.io.IOException { return 0.0f; }
	public final void readFully(byte[] var0) throws java.io.IOException { }
	public final void readFully(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public final int readInt() throws java.io.IOException { return 0; }
	public final java.lang.String readLine() throws java.io.IOException { return null; }
	public final long readLong() throws java.io.IOException { return 0l; }
	public final short readShort() throws java.io.IOException { return 0; }
	public final int readUnsignedByte() throws java.io.IOException { return 0; }
	public final int readUnsignedShort() throws java.io.IOException { return 0; }
	public final java.lang.String readUTF() throws java.io.IOException { return null; }
	public void seek(long var0) throws java.io.IOException { }
	public void setLength(long var0) throws java.io.IOException { }
	public int skipBytes(int var0) throws java.io.IOException { return 0; }
	public void write(byte[] var0) throws java.io.IOException { }
	public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public void write(int var0) throws java.io.IOException { }
	public final void writeBoolean(boolean var0) throws java.io.IOException { }
	public final void writeByte(int var0) throws java.io.IOException { }
	public final void writeBytes(java.lang.String var0) throws java.io.IOException { }
	public final void writeChar(int var0) throws java.io.IOException { }
	public final void writeChars(java.lang.String var0) throws java.io.IOException { }
	public final void writeDouble(double var0) throws java.io.IOException { }
	public final void writeFloat(float var0) throws java.io.IOException { }
	public final void writeInt(int var0) throws java.io.IOException { }
	public final void writeLong(long var0) throws java.io.IOException { }
	public final void writeShort(int var0) throws java.io.IOException { }
	public final void writeUTF(java.lang.String var0) throws java.io.IOException { }
}

