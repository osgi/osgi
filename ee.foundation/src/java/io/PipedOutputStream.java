/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.io;
public class PipedOutputStream extends java.io.OutputStream {
    public PipedOutputStream() { }
    public PipedOutputStream(java.io.PipedInputStream var0) throws java.io.IOException { }
    public void close() throws java.io.IOException { }
    public void connect(java.io.PipedInputStream var0) throws java.io.IOException { }
    public void flush() throws java.io.IOException { }
    public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
    public void write(int var0) throws java.io.IOException { }
}

