/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.io;
public abstract interface DataInput {
    public abstract boolean readBoolean() throws java.io.IOException;
    public abstract byte readByte() throws java.io.IOException;
    public abstract char readChar() throws java.io.IOException;
    public abstract double readDouble() throws java.io.IOException;
    public abstract float readFloat() throws java.io.IOException;
    public abstract void readFully(byte[] var0) throws java.io.IOException;
    public abstract void readFully(byte[] var0, int var1, int var2) throws java.io.IOException;
    public abstract int readInt() throws java.io.IOException;
    public abstract java.lang.String readLine() throws java.io.IOException;
    public abstract long readLong() throws java.io.IOException;
    public abstract short readShort() throws java.io.IOException;
    public abstract int readUnsignedByte() throws java.io.IOException;
    public abstract int readUnsignedShort() throws java.io.IOException;
    public abstract java.lang.String readUTF() throws java.io.IOException;
    public abstract int skipBytes(int var0) throws java.io.IOException;
}

