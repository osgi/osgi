/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.util.zip;
public class InflaterInputStream extends java.io.FilterInputStream {
    public InflaterInputStream(java.io.InputStream var0) { super(null); }
    public InflaterInputStream(java.io.InputStream var0, java.util.zip.Inflater var1) { super(null); }
    public InflaterInputStream(java.io.InputStream var0, java.util.zip.Inflater var1, int var2) { super(null); }
    public int read() throws java.io.IOException { return 0; }
    public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
    protected void fill() throws java.io.IOException { }
    public long skip(long var0) throws java.io.IOException { return 0l; }
    public int available() throws java.io.IOException { return 0; }
    public void close() throws java.io.IOException { }
    protected java.util.zip.Inflater inf;
    protected byte[] buf;
    protected int len;
}

