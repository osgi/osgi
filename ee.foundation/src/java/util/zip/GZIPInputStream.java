/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util.zip;
public class GZIPInputStream extends java.util.zip.InflaterInputStream {
    public GZIPInputStream(java.io.InputStream var0) throws java.io.IOException { super(null, null, 0); }
    public GZIPInputStream(java.io.InputStream var0, int var1) throws java.io.IOException { super(null, null, 0); }
    public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
    public void close() throws java.io.IOException { }
    protected java.util.zip.CRC32 crc;
    protected boolean eos;
    public final static int GZIP_MAGIC = 35615;
}

