/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security;
public class DigestInputStream extends java.io.FilterInputStream {
    public DigestInputStream(java.io.InputStream var0, java.security.MessageDigest var1) { super(null); }
    public java.security.MessageDigest getMessageDigest() { return null; }
    public void on(boolean var0) { }
    public int read() throws java.io.IOException { return 0; }
    public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
    public void setMessageDigest(java.security.MessageDigest var0) { }
    public java.lang.String toString() { return null; }
    protected java.security.MessageDigest digest;
}

