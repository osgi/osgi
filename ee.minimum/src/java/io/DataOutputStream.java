/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.io;
public class DataOutputStream extends java.io.FilterOutputStream implements java.io.DataOutput {
    public DataOutputStream(java.io.OutputStream var0) { super(null); }
    public void flush() throws java.io.IOException { }
    public final int size() { return 0; }
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
    protected int written;
}

