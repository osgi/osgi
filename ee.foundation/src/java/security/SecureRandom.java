/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security;
public class SecureRandom extends java.util.Random {
    public SecureRandom() { }
    public SecureRandom(byte[] var0) { }
    protected SecureRandom(java.security.SecureRandomSpi var0, java.security.Provider var1) { }
    public byte[] generateSeed(int var0) { return null; }
    public static java.security.SecureRandom getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
    public static java.security.SecureRandom getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
    public final java.security.Provider getProvider() { return null; }
    public static byte[] getSeed(int var0) { return null; }
    protected final int next(int var0) { return 0; }
    public void nextBytes(byte[] var0) { }
    public void setSeed(byte[] var0) { }
    public void setSeed(long var0) { }
}

