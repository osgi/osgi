/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security;
public abstract class KeyFactorySpi {
    public KeyFactorySpi() { }
    protected abstract java.security.PrivateKey engineGeneratePrivate(java.security.spec.KeySpec var0) throws java.security.spec.InvalidKeySpecException;
    protected abstract java.security.PublicKey engineGeneratePublic(java.security.spec.KeySpec var0) throws java.security.spec.InvalidKeySpecException;
    protected abstract java.security.spec.KeySpec engineGetKeySpec(java.security.Key var0, java.lang.Class var1) throws java.security.spec.InvalidKeySpecException;
    protected abstract java.security.Key engineTranslateKey(java.security.Key var0) throws java.security.InvalidKeyException;
}

