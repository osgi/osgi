/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public class KeyFactory {
    protected KeyFactory(java.security.KeyFactorySpi var0, java.security.Provider var1, java.lang.String var2) { }
    public final java.security.PrivateKey generatePrivate(java.security.spec.KeySpec var0) throws java.security.spec.InvalidKeySpecException { return null; }
    public final java.security.PublicKey generatePublic(java.security.spec.KeySpec var0) throws java.security.spec.InvalidKeySpecException { return null; }
    public final java.lang.String getAlgorithm() { return null; }
    public static java.security.KeyFactory getInstance(java.lang.String var0) throws java.security.NoSuchAlgorithmException { return null; }
    public static java.security.KeyFactory getInstance(java.lang.String var0, java.lang.String var1) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException { return null; }
    public final java.security.spec.KeySpec getKeySpec(java.security.Key var0, java.lang.Class var1) throws java.security.spec.InvalidKeySpecException { return null; }
    public final java.security.Provider getProvider() { return null; }
    public final java.security.Key translateKey(java.security.Key var0) throws java.security.InvalidKeyException { return null; }
}

