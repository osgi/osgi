/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.io;
public abstract interface DataOutput {
	public abstract void write(byte[] var0) throws java.io.IOException;
	public abstract void write(byte[] var0, int var1, int var2) throws java.io.IOException;
	public abstract void write(int var0) throws java.io.IOException;
	public abstract void writeBoolean(boolean var0) throws java.io.IOException;
	public abstract void writeByte(int var0) throws java.io.IOException;
	public abstract void writeBytes(java.lang.String var0) throws java.io.IOException;
	public abstract void writeChar(int var0) throws java.io.IOException;
	public abstract void writeChars(java.lang.String var0) throws java.io.IOException;
	public abstract void writeDouble(double var0) throws java.io.IOException;
	public abstract void writeFloat(float var0) throws java.io.IOException;
	public abstract void writeInt(int var0) throws java.io.IOException;
	public abstract void writeLong(long var0) throws java.io.IOException;
	public abstract void writeShort(int var0) throws java.io.IOException;
	public abstract void writeUTF(java.lang.String var0) throws java.io.IOException;
}

