/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.io;
public class BufferedInputStream extends java.io.FilterInputStream {
    public BufferedInputStream(java.io.InputStream var0) { super(null); }
    public BufferedInputStream(java.io.InputStream var0, int var1) { super(null); }
    public int available() throws java.io.IOException { return 0; }
    public void close() throws java.io.IOException { }
    public void mark(int var0) { }
    public boolean markSupported() { return false; }
    public int read() throws java.io.IOException { return 0; }
    public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
    public void reset() throws java.io.IOException { }
    public long skip(long var0) throws java.io.IOException { return 0l; }
    protected byte[] buf;
    protected int count;
    protected int marklimit;
    protected int markpos;
    protected int pos;
}

