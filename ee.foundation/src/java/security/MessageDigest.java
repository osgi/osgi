/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public abstract class MessageDigest extends java.security.MessageDigestSpi {
    protected MessageDigest(java.lang.String var0) { }
    public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
    public byte[] digest() { return null; }
    public byte[] digest(byte[] var0) { return null; }
    public int digest(byte[] var0, int var1, int var2) throws java.security.DigestException { return 0; }
    public final java.lang.String getAlgorithm() { return null; }
    public final int getDigestLength() { return 0; }
    public static java.security.MessageDigest getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
    public static java.security.MessageDigest getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
    public final java.security.Provider getProvider() { return null; }
    public static boolean isEqual(byte[] var0, byte[] var1) { return false; }
    public void reset() { }
    public java.lang.String toString() { return null; }
    public void update(byte[] var0) { }
    public void update(byte[] var0, int var1, int var2) { }
    public void update(byte var0) { }
}

