/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.util.zip;
public class CheckedOutputStream extends java.io.FilterOutputStream {
    public CheckedOutputStream(java.io.OutputStream var0, java.util.zip.Checksum var1) { super(null); }
    public java.util.zip.Checksum getChecksum() { return null; }
    public void write(int var0) throws java.io.IOException { }
    public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
}

