/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.io;
public abstract class OutputStream {
    public OutputStream() { }
    public void close() throws java.io.IOException { }
    public void flush() throws java.io.IOException { }
    public void write(byte[] var0) throws java.io.IOException { }
    public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
    public abstract void write(int var0) throws java.io.IOException;
}

