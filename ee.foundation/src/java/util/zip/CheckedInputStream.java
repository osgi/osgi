/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util.zip;
public class CheckedInputStream extends java.io.FilterInputStream {
    public CheckedInputStream(java.io.InputStream var0, java.util.zip.Checksum var1) { super(null); }
    public int read() throws java.io.IOException { return 0; }
    public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
    public java.util.zip.Checksum getChecksum() { return null; }
    public long skip(long var0) throws java.io.IOException { return 0l; }
}

