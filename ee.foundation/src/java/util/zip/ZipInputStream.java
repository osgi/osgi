/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util.zip;
public class ZipInputStream extends java.util.zip.InflaterInputStream implements java.util.zip.ZipConstants {
    public ZipInputStream(java.io.InputStream var0) { super(null, null, 0); }
    public void close() throws java.io.IOException { }
    public void closeEntry() throws java.io.IOException { }
    public java.util.zip.ZipEntry getNextEntry() throws java.io.IOException { return null; }
    public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
    public long skip(long var0) throws java.io.IOException { return 0l; }
    public int available() throws java.io.IOException { return 0; }
    protected java.util.zip.ZipEntry createZipEntry(java.lang.String var0) { return null; }
}

