/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.io;
public class PipedInputStream extends java.io.InputStream {
    public PipedInputStream() { }
    public PipedInputStream(java.io.PipedOutputStream var0) throws java.io.IOException { }
    public int available() throws java.io.IOException { return 0; }
    public void close() throws java.io.IOException { }
    public void connect(java.io.PipedOutputStream var0) throws java.io.IOException { }
    public int read() throws java.io.IOException { return 0; }
    public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
    protected void receive(int var0) throws java.io.IOException { }
    protected byte[] buffer;
    protected int in;
    protected int out;
    protected final static int PIPE_SIZE = 1024;
}

