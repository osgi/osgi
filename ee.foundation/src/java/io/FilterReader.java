/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.io;
public abstract class FilterReader extends java.io.Reader {
    protected FilterReader(java.io.Reader var0) { }
    public void close() throws java.io.IOException { }
    public void mark(int var0) throws java.io.IOException { }
    public boolean markSupported() { return false; }
    public int read() throws java.io.IOException { return 0; }
    public int read(char[] var0, int var1, int var2) throws java.io.IOException { return 0; }
    public boolean ready() throws java.io.IOException { return false; }
    public void reset() throws java.io.IOException { }
    public long skip(long var0) throws java.io.IOException { return 0l; }
    protected java.io.Reader in;
}

