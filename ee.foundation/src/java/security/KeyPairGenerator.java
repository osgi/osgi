/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public abstract class KeyPairGenerator extends java.security.KeyPairGeneratorSpi {
    protected KeyPairGenerator(java.lang.String var0) { }
    public final java.security.KeyPair genKeyPair() { return null; }
    public java.lang.String getAlgorithm() { return null; }
    public static java.security.KeyPairGenerator getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
    public static java.security.KeyPairGenerator getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
    public final java.security.Provider getProvider() { return null; }
    public void initialize(int var0) { }
    public void initialize(int var0, java.security.SecureRandom var1) { }
    public void initialize(java.security.spec.AlgorithmParameterSpec var0) throws java.security.InvalidAlgorithmParameterException { }
    public void initialize(java.security.spec.AlgorithmParameterSpec var0, java.security.SecureRandom var1) throws java.security.InvalidAlgorithmParameterException { }
    public java.security.KeyPair generateKeyPair() { return null; }
}

