/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.io;
public abstract interface ObjectOutput extends java.io.DataOutput {
    public abstract void close() throws java.io.IOException;
    public abstract void flush() throws java.io.IOException;
    public abstract void write(byte[] var0) throws java.io.IOException;
    public abstract void write(byte[] var0, int var1, int var2) throws java.io.IOException;
    public abstract void write(int var0) throws java.io.IOException;
    public abstract void writeObject(java.lang.Object var0) throws java.io.IOException;
}

