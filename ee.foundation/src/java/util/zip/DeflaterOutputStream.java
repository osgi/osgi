/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.util.zip;
public class DeflaterOutputStream extends java.io.FilterOutputStream {
    public DeflaterOutputStream(java.io.OutputStream var0, java.util.zip.Deflater var1) { super(null); }
    public DeflaterOutputStream(java.io.OutputStream var0) { super(null); }
    public DeflaterOutputStream(java.io.OutputStream var0, java.util.zip.Deflater var1, int var2) { super(null); }
    protected void deflate() throws java.io.IOException { }
    public void close() throws java.io.IOException { }
    public void finish() throws java.io.IOException { }
    public void write(int var0) throws java.io.IOException { }
    public void write(byte[] var0, int var1, int var2) throws java.io.IOException { }
    protected byte[] buf;
    protected java.util.zip.Deflater def;
}

