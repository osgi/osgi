/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package javax.microedition.io;
public abstract interface Datagram extends java.io.DataInput, java.io.DataOutput {
    public abstract java.lang.String getAddress();
    public abstract byte[] getData();
    public abstract int getLength();
    public abstract int getOffset();
    public abstract void reset();
    public abstract void setAddress(javax.microedition.io.Datagram var0);
    public abstract void setAddress(java.lang.String var0) throws java.io.IOException;
    public abstract void setData(byte[] var0, int var1, int var2);
    public abstract void setLength(int var0);
}

