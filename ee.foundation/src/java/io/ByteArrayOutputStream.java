/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.io;
public class ByteArrayOutputStream extends java.io.OutputStream {
    public ByteArrayOutputStream() { }
    public ByteArrayOutputStream(int var0) { }
    public void close() throws java.io.IOException { }
    public void reset() { }
    public int size() { return 0; }
    public byte[] toByteArray() { return null; }
    public java.lang.String toString() { return null; }
    public java.lang.String toString(java.lang.String var0) throws java.io.UnsupportedEncodingException { return null; }
    public void write(byte[] var0, int var1, int var2) { }
    public void write(int var0) { }
    public void writeTo(java.io.OutputStream var0) throws java.io.IOException { }
    protected byte[] buf;
    protected int count;
}

