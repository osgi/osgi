/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util.zip;
public class GZIPOutputStream extends java.util.zip.DeflaterOutputStream {
    public GZIPOutputStream(java.io.OutputStream var0) throws java.io.IOException { super(null, null, 0); }
    public GZIPOutputStream(java.io.OutputStream var0, int var1) throws java.io.IOException { super(null, null, 0); }
    public void finish() throws java.io.IOException { }
    public void close() throws java.io.IOException { }
    public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
    protected java.util.zip.CRC32 crc;
}

