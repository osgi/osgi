/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.io;
public class CharArrayReader extends java.io.Reader {
    public CharArrayReader(char[] var0) { }
    public CharArrayReader(char[] var0, int var1, int var2) { }
    public void close() { }
    public void mark(int var0) throws java.io.IOException { }
    public boolean markSupported() { return false; }
    public int read() throws java.io.IOException { return 0; }
    public int read(char[] var0, int var1, int var2) throws java.io.IOException { return 0; }
    public boolean ready() throws java.io.IOException { return false; }
    public void reset() throws java.io.IOException { }
    public long skip(long var0) throws java.io.IOException { return 0l; }
    protected char[] buf;
    protected int pos;
    protected int markedPos;
    protected int count;
}

